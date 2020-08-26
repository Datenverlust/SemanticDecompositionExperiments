package arc

import arc.dataset.allTextElements
import arc.negation.findNegationTargets
import arc.negation.resolveNegation
import arc.srl.identifySemanticRoles
import arc.util.addGraph
import arc.util.asAnnotatedCoreDocument
import arc.util.createEdge
import arc.util.decompose
import arc.util.isStopWord
import arc.util.mapIf
import arc.util.merge
import arc.util.syntaxEdges
import arc.wsd.disambiguateBy
import arc.wsd.markContext
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.edges.EdgeType
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing
import de.kimanufaktur.nsm.graph.entities.marker.DoubleMarkerWithOrigin
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.CoreDocument
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultListenableGraph
import java.util.Collections

val markerPassingConfig = ArcMarkerPassingConfig()

class ArcSolver : (ArcTask) -> ArcResult {

    private val graphComponentCache = Collections.synchronizedMap<String, GraphComponent>(mutableMapOf())

    private fun String.asKey(config: ArcGraphConfig) = "$this#${config.depth}"

    private val semanticGraphCache = Collections.synchronizedMap<String, DefaultListenableGraph<Concept, WeightedEdge>>(mutableMapOf())

    private fun Concept.asKey(config: ArcGraphConfig) = "${hashCode()}#${config.hashCode()}"

    private fun DefaultListenableGraph<Concept, WeightedEdge>.addSemanticRelationsRecursive(source: Concept, config: ArcGraphConfig) {
        if (!source.isStopWord() && !containsVertex(source)) {
            addVertex(source)
            addDefinitionsBy(source, config)
            addConceptsBy(source, config)
        }
    }

    private fun DefaultListenableGraph<Concept, WeightedEdge>.addDefinitionsBy(source: Concept, config: ArcGraphConfig) {
        if (source.assignedSenseKeys.isEmpty()) {
            source.definitions
        } else {
            source.definitions.filter { def -> def.sensekey in source.assignedSenseKeys }
        }
            .mapNotNull { def ->
                def.gloss?.toGraphComponent(
                    config = config.copy(depth = config.depth.dec())
                )
            }
            .also { defComponents ->
                addGraph(defComponents.map { component -> component.graph }.merge())
                defComponents.map { it.conceptMap.values }
                    .flatten()
                    .distinct()
                    .forEach { target ->
                        if (!target.isStopWord()) {
                            createEdge(
                                edgeType = EdgeType.Definition,
                                source = source,
                                target = target
                            )
                                ?.let { edge ->
                                    if (!containsEdge(edge)) {
                                        addEdge(source, target, edge)
                                        setEdgeWeight(edge, edge.edgeWeight)
                                    }
                                }
                        }
                    }
            }
    }

    private fun DefaultListenableGraph<Concept, WeightedEdge>.addConceptsBy(source: Concept, config: ArcGraphConfig) {
        mapOf(
            EdgeType.Synonym to source.senseKeyToSynonymsMap,
            EdgeType.Antonym to source.senseKeyToAntonymsMap,
            EdgeType.Hyponym to source.senseKeyToHyponymsMap,
            EdgeType.Hypernym to source.senseKeyToHypernymsMap,
            EdgeType.Meronym to source.senseKeyToMeronymsMap
        )
            .mapValues { (_, senseKeyMap) ->
                if (source.assignedSenseKeys.isEmpty()) senseKeyMap.values.flatten()
                else source.assignedSenseKeys.mapNotNull { senseKey -> senseKeyMap[senseKey] }.flatten()
            }
            .map { (edgeType, conceptList) ->
                conceptList.map { con -> edgeType to con }
            }
            .flatten()
            .filterNot { (_, target) -> source == target || target.isStopWord() }
            .forEach { (edgeType, target) ->
                if (!target.isStopWord()) {
                    addSemanticRelationsRecursive(target, config.copy(depth = config.depth.dec()))
                    createEdge(
                        edgeType = edgeType,
                        source = source,
                        target = target
                    )
                        ?.let { edge ->
                            if (!containsEdge(edge)) {
                                addEdge(source, target, edge)
                                setEdgeWeight(edge, edge.edgeWeight)
                            }
                        }
                }
            }
    }

    private fun DefaultListenableGraph<Concept, WeightedEdge>.addSemanticGraph(
        conceptList: Collection<Concept>,
        config: ArcGraphConfig
    ) {
        conceptList.map { source ->
            val key = source.asKey(config)
            semanticGraphCache[key]
                ?: DefaultListenableGraph(DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java))
                    .also { graph -> graph.addSemanticRelationsRecursive(source, config) }
                    .also { semanticGraphCache[key] = it }
        }
            .merge()
            .also { graph -> addGraph(graph) }
    }

    private fun DefaultListenableGraph<Concept, WeightedEdge>.addSyntaxGraph(
        conceptMap: Map<CoreLabel, Concept>,
        coreDoc: CoreDocument
    ) {
        coreDoc.syntaxEdges()
            .forEach { syntaxEdge ->
                syntaxEdge.source.backingLabel().let { source -> conceptMap[source] }
                    ?.let { source ->
                        syntaxEdge.target.backingLabel().let { target -> conceptMap[target] }
                            ?.let { target ->
                                createEdge(
                                    edgeType = EdgeType.Syntax,
                                    source = source,
                                    target = target,
                                    relation = syntaxEdge.relation.longName
                                )
                                    ?.let { edge ->
                                        if (!containsEdge(edge)) {
                                            addEdge(source, target, edge)
                                            setEdgeWeight(edge, edge.edgeWeight)
                                        }
                                    }
                            }
                    }

            }
    }

    private fun DefaultListenableGraph<Concept, WeightedEdge>.addNerGraph(
        conceptMap: Map<CoreLabel, Concept>,
        config: ArcGraphConfig
    ) {
        conceptMap
            .filterKeys { it.ner() != null && it.ner() != "O" }
            .forEach { (nameCoreLabel, nameConcept) ->
                nameCoreLabel.ner().toLowerCase().decompose(config, nameCoreLabel.tag())
                    .also { entityConcept -> addSemanticRelationsRecursive(entityConcept, config) }
                    .let { entityConcept ->
                        createEdge(
                            edgeType = EdgeType.NamedEntity,
                            source = nameConcept,
                            target = entityConcept
                        )?.let { edge ->
                            if (!containsEdge(edge)) {
                                addEdge(nameConcept, entityConcept, edge)
                                setEdgeWeight(edge, edge.edgeWeight)
                            }
                        }
                    }
            }
    }

    private fun DefaultListenableGraph<Concept, WeightedEdge>.addRolesGraph(
        conceptMap: Map<CoreLabel, Concept>,
        coreDoc: CoreDocument,
        config: ArcGraphConfig
    ) {
        identifySemanticRoles(coreDoc)
            .forEach { (coreLabel, roleList) ->
                conceptMap[coreLabel]?.let { source ->
                    roleList
                        .map { role ->
                            role.toGraphComponent(
                                config.copy(
                                    useSrl = false,
                                    depth = config.depth.dec()
                                )
                            ).graph
                        }
                        .forEach { roleGraph ->
                            roleGraph.vertexSet().forEach { addVertex(it) }
                            roleGraph.edgeSet().forEach { edge -> addEdge(edge.source as Concept, edge.target as Concept, edge) }
                            roleGraph.vertexSet()
                                .forEach { roleConcept ->
                                    createEdge(
                                        edgeType = EdgeType.SemanticRole,
                                        source = source,
                                        target = roleConcept
                                    )?.let { edge ->
                                        if (!containsEdge(edge)) {
                                            addEdge(source, roleConcept, edge)
                                            setEdgeWeight(edge, edge.edgeWeight)
                                        }
                                    }
                                }
                        }
                }
            }
    }

    fun String.toGraphComponent(config: ArcGraphConfig = ArcGraphConfig()): GraphComponent {
        val key = asKey(config)
        graphComponentCache[key]?.let { return it }

        val coreDoc = asAnnotatedCoreDocument()
        val conceptMap = coreDoc.toConceptMap(config)
        val graph = DefaultListenableGraph(
            DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
        ).also { graph ->
            conceptMap.values.forEach { graph.addVertex(it) }
            if (config.depth > 0) {
                if (config.useSemDec) graph.addSemanticGraph(conceptMap.values, config)
                if (config.useSyntax) graph.addSyntaxGraph(conceptMap, coreDoc)
                if (config.useNer) graph.addNerGraph(conceptMap, config)
                if (config.useSrl) graph.addRolesGraph(conceptMap, coreDoc, config)
            }
        }
        return GraphComponent(
            context = this,
            coreDoc = coreDoc,
            conceptMap = conceptMap,
            graph = graph
        ).also { graphComponentCache[key] = it }
    }

    private fun CoreDocument.toConceptMap(config: ArcGraphConfig): Map<CoreLabel, Concept> {
        val tokens by lazy { tokens() }
        val words by lazy { tokens().map { it.word() } }
        val negTargets by lazy { findNegationTargets() }

        return sentences().asSequence()
            .map { coreSentence ->
                coreSentence.tokens().asSequence()
                    .filterNot { coreLabel ->
                        coreLabel.originalText().replace("""[^\p{Alnum}]+""".toRegex(), "").isBlank()
                            || coreLabel.lemma().isStopWord()
                            || coreLabel.originalText().isStopWord()
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
            .mapIf(config.useNeg && config.depth > 0) { (coreLabel, concept) ->
                coreLabel to if (coreLabel in negTargets) concept.resolveNegation(
                    config = config,
                    markedContext = tokens.indexOf(coreLabel).markContext(words)
                ) else concept
            }
            .toMap()
    }

    fun buildGraphComponent(text: String, config: ArcGraphConfig = ArcGraphConfig()): GraphComponent = text.toGraphComponent(config)

    override fun invoke(task: ArcTask): ArcResult {
        graphComponentCache.clear()

        val allComponents = task.allTextElements().map { elem -> elem.toGraphComponent() }

        return listOf(
            ArcLabel.W0,
            ArcLabel.W1
        )
            .map { label ->
                val (warrant, components) =
                    if (label == ArcLabel.W0)
                        task.warrant0 to allComponents.filterNot { it.context == task.warrant1 }
                    else
                        task.warrant1 to allComponents.filterNot { it.context == task.warrant0 }

                val graph = components.map { it.graph }.merge()

                val markerPassing = ArcMarkerPassing(
                    graph = graph,
                    threshold = components.map { it.conceptMap.values }.flatten().createThresholdMap(),
                    nodeType = DoubleNodeWithMultipleThresholds::class.java
                )
                    .also { markerPassing ->
                        components.filter { it.context in listOf(task.reason, warrant) }
                            .map { it.conceptMap.values }
                            .flatten()
                            .createStartActivationMap()
                            .let { startActivationMap ->
                                markerPassing.doInitialMarking(startActivationMap)
                            }
                    }
                    .also { it.execute() }
                ArcPartialResult(
                    score = markerPassing.activationMap()
                        .evaluate(components.map { it.conceptMap.values }.flatten()),
                    numVertices = graph.vertexSet().size,
                    numEdges = graph.edgeSet().size
                )
            }
            .let { it[0] to it[1] }
            .let { (resultW0, resultW1) ->
                ArcResult(
                    id = task.id,
                    foundLabel =
                    when {
                        resultW0.score > resultW1.score -> ArcLabel.W0
                        resultW0.score < resultW1.score -> ArcLabel.W1
                        else -> ArcLabel.UNKNOWN
                    },
                    correctLabel = task.correctLabelW0orW1,
                    resultW0 = resultW0,
                    resultW1 = resultW1
                )
            }
    }

    private fun Collection<Concept>.createStartActivationMap() = map { concept ->
        DoubleMarkerWithOrigin().also {
            it.activation = markerPassingConfig.startActivation
            it.origin = concept
        }
            .let { marker -> concept to listOf(marker) }
    }
        .toMap()

    private fun Collection<Concept>.createThresholdMap() = map { concept -> concept to markerPassingConfig.threshold }.toMap()

    private fun DoubleMarkerPassing.activationMap() = nodes
        .map { (_, node) -> node as DoubleNodeWithMultipleThresholds }
        .map { node -> node.activationHistory.map { it as DoubleMarkerWithOrigin } }
        .flatten()
        .map { it.origin to it.activation }
        .toMap()

    private fun Map<Concept, Double>.evaluate(
        resultConcepts: Collection<Concept>
    ) = resultConcepts.mapNotNull { concept -> this[concept] }.average()

}


