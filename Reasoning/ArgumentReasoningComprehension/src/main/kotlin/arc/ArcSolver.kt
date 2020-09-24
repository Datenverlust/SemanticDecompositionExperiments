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
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig
import de.kimanufaktur.nsm.graph.entities.marker.DoubleMarkerWithOrigin
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.CoreDocument
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultListenableGraph
import java.util.Collections
import kotlin.random.Random


class ArcSolver : (ArcTask, ArcConfig) -> ArcResult {

    val graphComponentCache: MutableMap<String, GraphData> = Collections.synchronizedMap(mutableMapOf())!!

    private fun String.asKey(config: ArcConfig) = "$this#${config.depth}"

    private val semanticGraphCache = Collections.synchronizedMap<String, DefaultListenableGraph<Concept, WeightedEdge>>(mutableMapOf())

    private fun Concept.asKey(config: ArcConfig) = "${hashCode()}#${config.hashCode()}"

    private fun DefaultListenableGraph<Concept, WeightedEdge>.addSemanticRelationsRecursive(source: Concept, config: ArcConfig) {
        if (!source.isStopWord() && !containsVertex(source)) {
            addVertex(source)
            addDefinitionsBy(source, config)
            addConceptsBy(source, config)
        }
    }

    private fun DefaultListenableGraph<Concept, WeightedEdge>.addDefinitionsBy(source: Concept, config: ArcConfig) {
        if (source.assignedSenseKeys.isEmpty()) {
            source.definitions
        } else {
            source.definitions.filter { def -> def.sensekey in source.assignedSenseKeys }
        }
            .mapNotNull { def ->
                def.gloss?.toGraphData(
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
                                    if (!containsEdge(edge)) addEdge(source, target, edge)
                                }
                        }
                    }
            }
    }

    private fun DefaultListenableGraph<Concept, WeightedEdge>.addConceptsBy(source: Concept, config: ArcConfig) {
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
                            if (!containsEdge(edge)) addEdge(source, target, edge)
                        }
                }
            }
    }

    private fun DefaultListenableGraph<Concept, WeightedEdge>.addSemanticGraph(
        conceptList: Collection<Concept>,
        config: ArcConfig
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
                                        if (!containsEdge(edge)) addEdge(source, target, edge)
                                    }
                            }
                    }

            }
    }

    private fun DefaultListenableGraph<Concept, WeightedEdge>.addNerGraph(
        conceptMap: Map<CoreLabel, Concept>,
        config: ArcConfig
    ) {
        conceptMap
            .filterKeys { it.ner() != null && it.ner() != "O" }
            .forEach { (nameCoreLabel, nameConcept) ->
                nameCoreLabel.ner().toLowerCase().decompose(config, nameCoreLabel.tag()) //TODO: warum kein disambiguate?
                    .also { entityConcept -> addSemanticRelationsRecursive(entityConcept, config) }
                    .let { entityConcept ->
                        createEdge(
                            edgeType = EdgeType.NamedEntity,
                            source = nameConcept,
                            target = entityConcept
                        )?.let { edge ->
                            if (!containsEdge(edge)) addEdge(nameConcept, entityConcept, edge)
                        }
                    }
            }
    }

    private fun DefaultListenableGraph<Concept, WeightedEdge>.addRolesGraph(
        conceptMap: Map<CoreLabel, Concept>,
        coreDoc: CoreDocument,
        config: ArcConfig
    ) {
        identifySemanticRoles(coreDoc)
            .forEach { (coreLabel, roleList) ->
                conceptMap[coreLabel]?.let { source ->
                    roleList
                        .map { role ->
                            role.toGraphData(
                                config.copy(
                                    useSrl = false,
                                    depth = config.depth.dec()
                                )
                            )
                        }
                        .forEach { roleData ->
                            val roleGraph = roleData.graph
                            roleGraph.vertexSet().forEach { addVertex(it) }
                            roleGraph.edgeSet().forEach { edge -> addEdge(edge.source as Concept, edge.target as Concept, edge) }
                            roleData.conceptMap.values
                                .forEach { roleConcept ->
                                    createEdge(
                                        edgeType = EdgeType.SemanticRole,
                                        source = source,
                                        target = roleConcept
                                    )?.let { edge ->
                                        if (!containsEdge(edge)) addEdge(source, roleConcept, edge)
                                    }
                                }
                        }
                }
            }
    }

    fun String.toGraphData(config: ArcConfig = ArcConfig()): GraphData {
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
        return GraphData(
            context = this,
            coreDoc = coreDoc,
            conceptMap = conceptMap,
            graph = graph
        ).also { graphComponentCache[key] = it }
    }

    private fun CoreDocument.toConceptMap(config: ArcConfig): Map<CoreLabel, Concept> {
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

    fun createGraphData(text: String, config: ArcConfig = ArcConfig()): GraphData = text.toGraphData(config)

    fun clearCaches() {
        semanticGraphCache.clear()
        graphComponentCache.clear()
    }

    override fun invoke(task: ArcTask, config: ArcConfig): ArcResult {
        val allComponents = task.allTextElements().map { elem -> elem.toGraphData() }

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
                currentConfig = config

                val startActivationMap = components.filter { it.context == task.reason }
                    .map { it.conceptMap.values }
                    .flatten()
                    .createStartActivationMap(config)

                val markerPassing = ArcMarkerPassing(
                    graph = graph,
                    threshold = components.map { it.conceptMap.values }.flatten().createThresholdMap(config),
                    nodeType = DoubleNodeWithMultipleThresholds::class.java
                )
                markerPassing.doInitialMarking(startActivationMap)
                MarkerPassingConfig.setTerminationPulsCount(config.pulseCount)
                markerPassing.execute()

                val activationMap = markerPassing.activationMap()

                val score = sequenceOf(
                    task.reason to task.claim,
                    warrant to task.claim,
                    task.reason to warrant
                )
                    .map { (origin, resulting) ->
                        components.first { it.context == origin }.conceptMap.values to
                            components.first { it.context == resulting }.conceptMap.values
                    }
                    .map { (originCons, resultCons) ->
                        activationMap.evaluate(originCons, resultCons)
                    }
                    .toList()

                label to ArcPartialResult(
                    score = score,
                    numVertices = graph.vertexSet().size,
                    numEdges = graph.edgeSet().size
                )
            }
            .toMap()
            .let { results ->
                val resultW0 = results.getValue(ArcLabel.W0)
                val resultW1 = results.getValue(ArcLabel.W1)
                val (resultLabel, index) = (resultW0.score.indices).mapNotNull { index ->
                    when {
                        resultW0.score[index] > resultW1.score[index] -> ArcLabel.W0 to index
                        resultW0.score[index] < resultW1.score[index] -> ArcLabel.W1 to index
                        else -> null
                    }
                }.firstOrNull()
                    ?: ArcLabel.UNKNOWN to 4//TODO: replace by: if (Random.nextBoolean()) ArcLabel.W0 else ArcLabel.W1

                ArcResult(
                    id = task.id,
                    foundLabel = resultLabel,
                    index = index,
                    correctLabel = task.correctLabelW0orW1,
                    resultW0 = resultW0,
                    resultW1 = resultW1
                )
            }
    }


    private fun Collection<Concept>.createStartActivationMap(config: ArcConfig) = map { concept ->
        DoubleMarkerWithOrigin().also {
            it.activation = config.startActivation
            it.origin = concept
        }
            .let { marker -> concept to listOf(marker) }
    }
        .toMap()

    private fun Collection<Concept>.createThresholdMap(config: ArcConfig) = map { concept -> concept to config.threshold }.toMap()

    private fun DoubleMarkerPassing.activationMap() = nodes
        .map { (_, node) -> node as DoubleNodeWithMultipleThresholds }
        .mapNotNull { node ->
            node.activationHistory.asSequence()
                .map { it as DoubleMarkerWithOrigin }
                .groupBy { it.origin }
                .filter { (_, markers) -> markers.isNotEmpty() }
                .mapValues { (_, markers) ->
                    markers.map { it.activation }.average()
                }
                .ifEmpty { null }
                ?.let { node.concept to it }
        }
        .toMap()

    private fun Map<Concept, Map<Concept, Double>>.evaluate(
        originCons: Collection<Concept>,
        resultCons: Collection<Concept>
    ): Double = resultCons.mapNotNull { target ->
        this[target]
            ?.filterKeys { origin -> origin in originCons }
            ?.values
    }
        .flatten()
        .average()
}