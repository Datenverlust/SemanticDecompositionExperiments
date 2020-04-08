package arc

import arc.wsd.WSDRequest
import arc.wsd.WordSense
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.Decomposition
import de.kimanufaktur.nsm.decomposition.graph.edges.AntonymEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.DefinitionEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.EdgeType
import de.kimanufaktur.nsm.decomposition.graph.edges.HypernymEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.HyponymEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.MeronymEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.SynonymEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultListenableGraph

val decomposition = Decomposition()

internal fun disambiguateByContext(word: Concept, sentence: String): List<String> {
    val wordSenses = word.getWordSenses()
    word.getTargetIndex(sentence)
        .map { target ->
            WSDRequest(
                targetIndex = target,
                sentence = sentence,
                wordSenses = wordSenses
            )
        }
        .let {
            return word.availableSensekeys.toList().shuffled().take(2)//wsdClient.disambiguate(it)
        }
}

//TODO: Rework this fun with use of tokenization by stanford core nlp pipeline
internal fun Concept.getTargetIndex(sentence: String): List<Int> {
    return sentence.split("""\s+""".toRegex())
        .mapIndexedNotNull { index, word ->
            if (this.litheral == word) index
            else null
        }
}

internal fun Concept.getWordSenses() = this.sensekeyToDefinitionsMap
    .toList()
    .map { (id, glossDef) ->
        WordSense(
            gloss = glossDef.toString(),
            senseKey = id
        )
    }

fun getGraph(word: Concept, decompositionDepth: Int = MarkerPassingConfig.getDecompositionDepth(), context: String): Graph<*, *> {
    //TODO: Use Graph Cache
    val decomposed = decomposition.decompose(word, decompositionDepth)
    decomposition.cleanUp()
    val senseKeys = disambiguateByContext(decomposed, context)
    return createJGraph(decomposed, senseKeys)
}

fun createJGraph(concept: Concept, senseKeys: List<String>) = DefaultListenableGraph(
    DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
)
    .also { graph ->
        if (concept !in Decomposition.getConcepts2Ignore()) {
            addConceptRecursivly(graph, concept, senseKeys)
        }
    }


fun createEdge(edgeType: EdgeType, source: Concept, target: Concept): WeightedEdge? {
    val edgetypeAttribute = "edgeType"
    return when (edgeType) {
        EdgeType.Synonym -> createEdge(
            edge = SynonymEdge(),
            source = source,
            target = target,
            weight = MarkerPassingConfig.getSynonymLinkWeight(),
            attributes = mapOf(edgetypeAttribute to "synonym")
        )
        EdgeType.Definition -> createEdge(
            edge = DefinitionEdge(),
            source = source,
            target = target,
            weight = MarkerPassingConfig.getDefinitionLinkWeight(),
            attributes = mapOf(edgetypeAttribute to "definition")
        )
        EdgeType.Hypernym -> createEdge(
            edge = HypernymEdge(),
            source = source,
            target = target,
            weight = MarkerPassingConfig.getHypernymLinkWeight(),
            attributes = mapOf(edgetypeAttribute to "hypernym")
        )
        EdgeType.Hyponym -> createEdge(
            edge = HyponymEdge(),
            source = source,
            target = target,
            weight = MarkerPassingConfig.getHyponymLinkWeight(),
            attributes = mapOf(edgetypeAttribute to "hyponym")
        )
        EdgeType.Antonym -> createEdge(
            edge = AntonymEdge(),
            source = source,
            target = target,
            weight = MarkerPassingConfig.getAntonymLinkWeight(),
            attributes = mapOf(edgetypeAttribute to "antonym")
        )
        EdgeType.Meronym -> createEdge(
            edge = MeronymEdge(),
            source = source,
            target = target,
            weight = MarkerPassingConfig.getMeronymLinkWeight(),
            attributes = mapOf(edgetypeAttribute to "meronym")
        )
        else -> null
    }
}

fun createEdge(edge: WeightedEdge, source: Concept, target: Concept, weight: Double, attributes: Map<String, String>): WeightedEdge {
    edge.source = source
    edge.target = target
    edge.weight = weight
    edge.attributes = attributes
    return edge
}

fun addConceptRecursivly(graph: DefaultListenableGraph<Concept, WeightedEdge>, source: Concept, senseKeys: List<String> = source.availableSensekeys.toList()) {
    if (!Decomposition.getConcepts2Ignore().contains(source)) {
        if (!graph.containsVertex(source)) {
            graph.addVertex(source)

            mapOf(
                EdgeType.Synonym to source.sensekeyToSynonymsMap,
                EdgeType.Definition to source.sensekeyToDefinitionsMap.mapValues { (_, def) -> def.definition },
                EdgeType.Hyponym to source.sensekeyToHyponymsMap,
                EdgeType.Hypernym to source.sensekeyToHypernymsMap,
                EdgeType.Antonym to source.sensekeyToAntonymsMap,
                EdgeType.Meronym to source.sensekeyToMeronymsMap
            )
                .mapValues { (_, senseKeyMap) ->
                    senseKeyMap.filterKeys { it in senseKeys }.values.flatten()
                }
                .map { (edgeType, concepts) ->
                    concepts.mapNotNull { it?.let { edgeType to it } }
                }
                .flatten()
                .filterNot { (_, target) -> target in Decomposition.getConcepts2Ignore() || source == target }
                .forEach { (edgeType, target) ->
                    addConceptRecursivly(graph, target)
                    createEdge(
                        edgeType = edgeType,
                        source = source,
                        target = target
                    )
                        ?.let { edge ->
                            if (edge !in graph.getAllEdges(source, target)) {
                                graph.addEdge(source, target, edge)
                                graph.setEdgeWeight(edge, edge.edgeWeight)
                            }
                        }
                }
        }
    }
}

//TODO: Use MergedGraphCache
fun mergeGraph(acc: Graph<*, *>, graph: Graph<*, *>): Graph<Concept, WeightedEdge> {
    return DefaultListenableGraph(
        DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
    )
        .also {
            val vertices = acc.vertexSet().toList().plus(graph.vertexSet())

            val groups = vertices.map { it as Concept }.groupBy { it.litheral }


            val edges = acc.edgeSet().plus(graph.edgeSet())
            addVerticesOfGraph(it, vertices.iterator())
            addEdgesOfGraph(it, edges.iterator())
        }
}

fun addVerticesOfGraph(result: DefaultListenableGraph<Concept, WeightedEdge>, iterator: Iterator<*>) {
    while (iterator.hasNext()) {
        result.addVertex(iterator.next() as Concept)
    }
}

fun addEdgesOfGraph(result: DefaultListenableGraph<Concept, WeightedEdge>, iterator: Iterator<*>) {
    while (iterator.hasNext()) {
        val edge = iterator.next() as WeightedEdge
        result.addEdge(edge.source as Concept, edge.target as Concept, edge)
        result.setEdgeWeight(edge, edge.edgeWeight)
    }
}