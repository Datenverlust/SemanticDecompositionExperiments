package arc

import arc.cache.GraphCache
import arc.cache.SemanticGraphCache
import arc.dataset.allTextElements
import arc.negation.findNegationTargets
import arc.negation.resolveNegation
import arc.srl.identifySemanticRoles
import arc.util.addGraph
import arc.util.asAnnotatedCoreDocument
import arc.util.createEdge
import arc.util.decompose
import arc.util.extractMeta
import arc.util.isStopWord
import arc.util.mapIf
import arc.util.merge
import arc.util.printProgress
import arc.util.syntaxEdges
import arc.wsd.disambiguateBy
import arc.wsd.markContext
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.edges.EdgeType
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.StringDoubleMarkerPassing
import de.kimanufaktur.nsm.graph.entities.marker.StringDoubleMarkerWithOrigin
import de.kimanufaktur.nsm.graph.entities.nodes.StringDoubleNodeWithMultipleThresholds
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.CoreDocument
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultListenableGraph
import java.util.Collections


class ArcSolver : (ArcTask, ArcConfig) -> ArcResult {

    val graphComponentCache = GraphCache()
    private val semanticGraphCache = SemanticGraphCache()
    val ramCache: MutableMap<String, GraphData> = Collections.synchronizedMap(mutableMapOf())

    private fun Concept.asKey(config: ArcConfig) = "${asNodeIdentifier()}#${config.depth}${if (config.useWsd) "_wsd" else ""}${if (config.useNeg) "_neg" else ""}"

    fun initialFillRamCache(dataSet: List<ArcTask>) = dataSet
        .also { println("-filling ram cache-") }
        .asSequence()
        .printProgress(10)
        .map { it.allTextElements() }
        .flatten()
        .map { text -> text.asKey(ArcConfig()) }
        .forEach { key ->
            graphComponentCache.find(key)?.let { ramCache[key] = it }
        }

    private fun String.asKey(config: ArcConfig) = "$this#${config.depth}${if (config.useWsd) "_wsd" else ""}${if (config.useNeg) "_neg" else ""}"

    private fun DefaultListenableGraph<String, WeightedEdge>.addSemanticRelationsRecursive(source: Concept, config: ArcConfig) {
        val node = source.asNodeIdentifier()
        if (!source.isStopWord() && !containsVertex(node)) {
            addVertex(node)
            addDefinitionsBy(source, config)
            addConceptsBy(source, config)
        }
    }

    private fun DefaultListenableGraph<String, WeightedEdge>.addDefinitionsBy(source: Concept, config: ArcConfig) {
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
                defComponents.map { it.sourceConcepts }
                    .flatten()
                    .distinct()
                    .forEach { targetNode ->
                        val sourceNode = source.asNodeIdentifier()
                        if (!targetNode.isStopWord()) {
                            createEdge(
                                edgeType = EdgeType.Definition,
                                source = sourceNode,
                                target = targetNode
                            )
                                ?.let { edge ->
                                    if (!containsEdge(edge)) addEdge(sourceNode, targetNode, edge)
                                }
                        }
                    }
            }
    }

    private fun DefaultListenableGraph<String, WeightedEdge>.addConceptsBy(source: Concept, config: ArcConfig) {
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
                    val sourceNode = source.asNodeIdentifier()
                    val targetNode = target.asNodeIdentifier()
                    addSemanticRelationsRecursive(target, config.copy(depth = config.depth.dec()))
                    createEdge(
                        edgeType = edgeType,
                        source = sourceNode,
                        target = targetNode
                    )
                        ?.let { edge ->
                            if (!containsEdge(edge)) addEdge(sourceNode, targetNode, edge)
                        }
                }
            }
    }

    private fun DefaultListenableGraph<String, WeightedEdge>.addSemanticGraph(
        conceptList: Collection<Concept>,
        config: ArcConfig
    ) {
        conceptList.map { source ->
            val key = source.asKey(config)
            semanticGraphCache.find(key)
                ?: DefaultListenableGraph(DefaultDirectedWeightedGraph<String, WeightedEdge>(WeightedEdge::class.java))
                    .also { graph -> graph.addSemanticRelationsRecursive(source, config) }
                    .also {
                        semanticGraphCache.save(key, it)
                    }
        }
            .merge()
            .also { graph -> addGraph(graph) }
    }

    private fun DefaultListenableGraph<String, WeightedEdge>.addSyntaxGraph(
        conceptMap: Map<CoreLabel, String>,
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

    private fun DefaultListenableGraph<String, WeightedEdge>.addNerGraph(
        conceptMap: Map<CoreLabel, String>,
        config: ArcConfig
    ) {
        conceptMap
            .filterKeys { it.ner() != null && it.ner() != "O" }
            .forEach { (nameCoreLabel, nameNode) ->
                nameCoreLabel.ner().toLowerCase().decompose(config, nameCoreLabel.tag()) //TODO: warum kein disambiguate?
                    .also { entityConcept -> addSemanticRelationsRecursive(entityConcept, config) }
                    .let { entityConcept ->
                        val entityNode = entityConcept.asNodeIdentifier()
                        createEdge(
                            edgeType = EdgeType.NamedEntity,
                            source = nameNode,
                            target = entityNode
                        )?.let { edge ->
                            if (!containsEdge(edge)) addEdge(nameNode, entityNode, edge)
                        }
                    }
            }
    }

    private fun DefaultListenableGraph<String, WeightedEdge>.addRolesGraph(
        conceptMap: Map<CoreLabel, String>,
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
                            roleGraph.edgeSet().forEach { edge -> addEdge(edge.source as String, edge.target as String, edge) }
                            roleData.sourceConcepts
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

    fun String.toGraphData(config: ArcConfig): GraphData {
        val key = asKey(config)
        graphComponentCache.find(key)?.let { return it }

        val coreDoc = asAnnotatedCoreDocument()
        val conceptMap = coreDoc.toConceptMap(config)
        val nodeMap = conceptMap.mapValues { (_, concept) -> concept.asNodeIdentifier() }
        val graph = DefaultListenableGraph(
            DefaultDirectedWeightedGraph<String, WeightedEdge>(WeightedEdge::class.java)
        ).also { graph ->
            nodeMap.values.forEach { graph.addVertex(it) }
            if (config.depth > 0) {
                if (config.useSemDec) graph.addSemanticGraph(conceptMap.values, config)
                if (config.useSyntax) graph.addSyntaxGraph(nodeMap, coreDoc)
                if (config.useNer) graph.addNerGraph(nodeMap, config)
                if (config.useSrl) graph.addRolesGraph(nodeMap, coreDoc, config)
            }
        }
        return GraphData(
            context = this,
            sourceConcepts = nodeMap.values.toSet(),
            graph = graph
        ).also { graphComponentCache.save(key, it) }
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

    fun createGraphes(task: ArcTask, config: ArcConfig): List<DefaultListenableGraph<String, WeightedEdge>> {
        val allComponents = task.allTextElements().map { elem -> ramCache[elem.asKey(config)] ?: elem.toGraphData(config) }
        return listOf(
            ArcLabel.W0,
            ArcLabel.W1
        )
            .map { label ->
                val (_, components) =
                    if (label == ArcLabel.W0)
                        task.warrant0 to allComponents.filterNot { it.context == task.warrant1 }
                    else
                        task.warrant1 to allComponents.filterNot { it.context == task.warrant0 }

                components.map { it.graph }.merge()
            }
    }

    override fun invoke(task: ArcTask, config: ArcConfig): ArcResult {
        val allComponents = task.allTextElements().map { elem -> ramCache[elem.asKey(config)] ?: elem.toGraphData(config) }
        return listOf(
            ArcLabel.W0,
            ArcLabel.W1
        )
            .map { label ->
                val (_, components) =
                    if (label == ArcLabel.W0)
                        task.warrant0 to allComponents.filterNot { it.context == task.warrant1 }
                    else
                        task.warrant1 to allComponents.filterNot { it.context == task.warrant0 }

                val graph = components.map { it.graph }.merge()
                currentConfig = config

                val startActivationMap = components.filter { it.context == task.reason }
                    .map { it.sourceConcepts }
                    .flatten()
                    .createStartActivationMap(config)

                val markerPassing = ArcMarkerPassing(
                    graph = graph,
                    threshold = components.map { it.sourceConcepts }.flatten().createThresholdMap(config),
                    nodeType = StringDoubleNodeWithMultipleThresholds::class.java
                )
                markerPassing.doInitialMarking(startActivationMap)
                MarkerPassingConfig.setTerminationPulsCount(config.pulseCount)
                markerPassing.execute()

                val score = markerPassing.activationMap().evaluate(
                    originCons = components.first { it.context == task.reason }.sourceConcepts,
                    targetCons = components.first { it.context == task.claim }.sourceConcepts
                )

                label to ArcPartialResult(
                    score = score,
                    graphMeta = graph.extractMeta()
                )
            }
            .toMap()
            .let { results ->
                val resultW0 = results.getValue(ArcLabel.W0)
                val resultW1 = results.getValue(ArcLabel.W1)

                val resultLabel = when {
                    resultW0.score > resultW1.score -> ArcLabel.W0
                    resultW0.score < resultW1.score -> ArcLabel.W1
                    else -> ArcLabel.UNKNOWN
                }

                ArcResult(
                    id = task.id,
                    foundLabel = resultLabel,
                    correctLabel = task.correctLabelW0orW1,
                    resultW0 = resultW0,
                    resultW1 = resultW1
                )
            }
    }


    private fun <T> Collection<T>.createStartActivationMap(config: ArcConfig) = map { concept ->
        StringDoubleMarkerWithOrigin().also {
            it.activation = config.startActivation
            it.origin = concept as String
        }
            .let { marker -> concept to listOf(marker) }
    }
        .toMap()

    private fun <T> Collection<T>.createThresholdMap(config: ArcConfig) = map { concept -> concept to config.threshold }.toMap()

    private fun StringDoubleMarkerPassing.activationMap() = nodes
        .map { (_, node) -> node as StringDoubleNodeWithMultipleThresholds }
        .mapNotNull { node ->
            node.activationHistory.asSequence()
                .map { it as StringDoubleMarkerWithOrigin }
                .groupBy { it.origin }
                .filter { (_, markers) -> markers.isNotEmpty() }
                .ifEmpty { null }
                ?.let { node.name to it }
        }
        .toMap()

    private fun Map<String, Map<String, List<StringDoubleMarkerWithOrigin>>>.evaluate(
        originCons: Collection<String>,
        targetCons: Collection<String>
    ): Double = targetCons.mapNotNull { target ->
        this[target]
            ?.filterKeys { origin -> origin in originCons }
            ?.values
            ?.mapNotNull { markerList ->
                markerList
//                    .filter { marker -> marker.visitedStrings.any { it in midCons } } TODO: filter by visited waypoints
                    .map { it.activation }
                    .ifEmpty { null }
                    ?.sum()
            }
            ?.sum()
    }
        .sum()
}