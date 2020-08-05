package arc

import arc.dataset.allElements
import arc.negation.resolveNegation
import arc.util.addNerGraph
import arc.util.addRolesGraph
import arc.util.addSemanticGraph
import arc.util.addSyntaxGraph
import arc.util.asAnnotatedCoreDocument
import arc.util.decompose
import arc.util.merge
import arc.util.syntaxEdges
import arc.wsd.disambiguateBy
import arc.wsd.markContext
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing
import de.kimanufaktur.nsm.graph.entities.marker.DoubleMarkerWithOrigin
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.CoreDocument
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultListenableGraph
import java.util.Collections
import arc.util.mapIf as mapIf

val graphCache = Collections.synchronizedMap<String, GraphComponent>(mutableMapOf())

fun String.asKey(config: ArcGraphConfig) = "$this#${config.depth}"

fun String.toGraphComponent(config: ArcGraphConfig = ArcGraphConfig()) =
    graphCache[asKey(config)] ?: buildGraphComponent(config)

fun String.buildGraphComponent(config: ArcGraphConfig = ArcGraphConfig()): GraphComponent {
    val coreDoc = asAnnotatedCoreDocument()
    val conceptMap = coreDoc.buildConceptMap(config)
    val graph = DefaultListenableGraph(
        DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
    ).also { graph ->
        conceptMap.values.forEach { graph.addVertex(it) }
        if (config.depth > 0) {
            if (config.useSemDec) graph.addSemanticGraph(conceptMap.values, config)
            if (config.useSyntax) graph.addSyntaxGraph(conceptMap, coreDoc, config)
            if (config.useNer) graph.addNerGraph(conceptMap, config)
            if (config.useSrl) graph.addRolesGraph(conceptMap, coreDoc, config)
        }
    }
    return GraphComponent(
        context = this,
        coreDoc = coreDoc,
        conceptMap = conceptMap,
        graph = graph
    ).also { graphCache[this.asKey(config)] = it }
}

fun CoreDocument.buildConceptMap(config: ArcGraphConfig): Map<CoreLabel, Concept> {
    val tokens by lazy { tokens() }
    val words by lazy { tokens().map { it.word() } }
    val negEdges by lazy { syntaxEdges().filter { it.relation.shortName == "neg" } }

    return sentences().asSequence()
        .map { coreSentence ->
            coreSentence.tokens().asSequence()
                .filterNot { coreLabel ->
                    coreLabel.originalText().replace("""[^\p{Alnum}]+""".toRegex(), "").isBlank()
                }
                .map { coreLabel ->
                    coreLabel to coreLabel.decompose(config)
                }
        }
        .flatten()
        .mapIf(config.useWsd && config.depth > 0) { (coreLabel, concept) ->
            coreLabel to concept.disambiguateBy(
                markedContext = tokens.indexOf(coreLabel).markContext(words)
            )
        }
        .mapIf(config.useNeg && config.depth > 0 && negEdges.isNotEmpty()) { (coreLabel, concept) ->
            coreLabel to concept.resolveNegation(
                config = config,
                coreLabel = coreLabel,
                markedContext = tokens.indexOf(coreLabel).markContext(words),
                negationEdges = negEdges
            )
        }
        .toMap()
}

fun ArcTask.solve(): ArcLabel {
    //TODO: simplify by data class
    val graphComponents = allElements().map { elem -> elem.toGraphComponent() }
    val thresholdConcepts = graphComponents.map { it.conceptMap.values }.flatten()
    val startActivationConcepts = graphComponents.first { it.context == reason }.conceptMap.values
    val w0Concepts = graphComponents.first { it.context == warrant0 }.conceptMap.values
    val w1Concepts = graphComponents.first { it.context == warrant1 }.conceptMap.values

    val graph = graphComponents.map { it.graph }.merge()

    val markerPassing = ArcMarkerPassing(
        graph,
        thresholdConcepts.createThresholdMap(),
        DoubleNodeWithMultipleThresholds::class.java
    )
        .also { markerPassing ->
            startActivationConcepts.createStartActivationMap().let { startActivationMap ->
                markerPassing.doInitialMarking(startActivationMap)
            }
        }
        .also { it.execute() }

    return markerPassing.activationMap()
        .evaluate(
            w0Concepts = w0Concepts,
            w1Concepts = w1Concepts
        )
}

private fun Collection<Concept>.createStartActivationMap() = map { concept ->
    DoubleMarkerWithOrigin().also {
        it.activation = startActivation
        it.origin = concept
    }
        .let { concept to listOf(it) }
}
    .toMap()

private fun Collection<Concept>.createThresholdMap() = map { concept -> concept to threshold }.toMap()

private fun DoubleMarkerPassing.activationMap() = nodes
    .map { (_, node) -> node as DoubleNodeWithMultipleThresholds }
    .map { node -> node.activationHistory.map { it as DoubleMarkerWithOrigin } }
    .flatten()
    .map { it.origin to it.activation }
    .toMap()


private fun Map<Concept, Double>.evaluate(
    w0Concepts: Collection<Concept>,
    w1Concepts: Collection<Concept>
) = mapOf(
    ArcLabel.W0 to w0Concepts,
    ArcLabel.W1 to w1Concepts
)
    .mapValues { (_, warrantConcepts) ->
        warrantConcepts.map { concept -> getValue(concept) }
            .average()
    }
    .toList()
    .minBy { (_, score) -> score }
    ?.let { (label, _) -> label }
    ?: ArcLabel.UNKNOWN
