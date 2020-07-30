package arc

import arc.dataset.allElements
import arc.util.asAnnotatedCoreDocument
import arc.util.createNerGraph
import arc.util.createRolesGraph
import arc.util.createSemanticGraph
import arc.util.createSyntaxGraph
import arc.util.decompose
import arc.util.isNamedEntity
import arc.util.isStopWord
import arc.util.merge
import arc.wsd.disambiguateBy
import arc.wsd.markContext
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.WordType
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing
import de.kimanufaktur.nsm.graph.entities.marker.DoubleMarkerWithOrigin
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.CoreDocument
import java.util.Collections

val graphCache = Collections.synchronizedMap<String, GraphComponent>(mutableMapOf())

fun String.asKey(config: ArcGraphConfig) = "$this#${config.decompositionDepth}"

fun String.toGraphComponent(config: ArcGraphConfig = ArcGraphConfig()) = graphCache[this.asKey(config)]
    ?: buildGraphComponent(config)

fun String.buildGraphComponent(config: ArcGraphConfig = ArcGraphConfig()): GraphComponent {
    val coreDoc = asAnnotatedCoreDocument()
    val conceptMap = coreDoc.buildConceptMap(config)

    val graph = listOfNotNull(
        if (config.useSemanticGraph)
            createSemanticGraph(
                conceptList = conceptMap.values,
                config = config
            )
        else null,
        if (config.useSyntaxDependencies) createSyntaxGraph(conceptMap, coreDoc) else null,
        if (config.useNamedEntities) createNerGraph(conceptMap, config) else null,
        if (config.useSemanticRoles) createRolesGraph(
            conceptMap = conceptMap,
            coreDoc = coreDoc,
            config = config
        ) else null
    )
        .merge()

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
    return sentences().map { coreSentence ->
        coreSentence.tokens().asSequence()
            .map { coreLabel ->
                coreLabel to coreLabel.originalText().replace("""[^\p{Alnum}]+""".toRegex(), "")
            }
            .filterNot { (_, litheral) -> litheral.isBlank() }
            .map { (coreLabel, litheral) ->
                Concept(litheral, WordType.getType(coreLabel.tag()))
                    .let { concept ->
                        if (concept.isStopWord() && coreLabel.isNamedEntity()) {
                            concept
                        } else {
                            concept.decompose(config.decompositionDepth)
                        }
                    }
                    .let { concept ->
                        if (config.useWsd) {
                            val markedContext = tokens.indexOf(coreLabel).markContext(words)
                            concept.disambiguateBy(markedContext)
                        } else concept
                    }
                    .let { if (config.useNegationHandling) it else it }
                    .let { concept -> coreLabel to concept }
            }
            .toList()
    }
        .flatten()
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
