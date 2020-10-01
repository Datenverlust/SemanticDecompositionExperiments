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

    val graphComponentCache = GraphCache()//: MutableMap<String, GraphData> = Collections.synchronizedMap(mutableMapOf())!!

    private fun String.asKey(config: ArcConfig) = "$this#${config.depth}"

    private val semanticGraphCache = SemanticGraphCache() // = Collections.synchronizedMap<String, DefaultListenableGraph<String, WeightedEdge>>(mutableMapOf())

    private fun Concept.asKey(config: ArcConfig) = "${asNodeIdentifier()}#${config.hashCode()}"

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
                defComponents.map { it.conceptMap.values }
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
            conceptMap = nodeMap,
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

    fun createGraphData(text: String, config: ArcConfig = ArcConfig()): GraphData = text.toGraphData(config)

    fun clearCaches() {
//        semanticGraphCache.clear()
//        graphComponentCache.clear()
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
                    nodeType = StringDoubleNodeWithMultipleThresholds::class.java
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
                val (index, resultLabel) = (resultW0.score.indices).mapNotNull { index ->
                    when {
                        resultW0.score[index] > resultW1.score[index] -> index to ArcLabel.W0
                        resultW0.score[index] < resultW1.score[index] -> index to ArcLabel.W1
                        else -> null
                    }
                }.firstOrNull()
                    ?: 4 to ArcLabel.UNKNOWN//TODO: replace by: 4 to if (Random.nextBoolean()) ArcLabel.W0 else ArcLabel.W1

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
                .mapValues { (_, markers) ->
                    markers.map { it.activation }.average()
                }
                .ifEmpty { null }
                ?.let { node.name to it }
        }
        .toMap()

    private fun <T> Map<T, Map<T, Double>>.evaluate(
        originCons: Collection<T>,
        resultCons: Collection<T>
    ): Double = resultCons.mapNotNull { target ->
        this[target]
            ?.filterKeys { origin -> origin in originCons }
            ?.values
    }
        .flatten()
        .average()
}