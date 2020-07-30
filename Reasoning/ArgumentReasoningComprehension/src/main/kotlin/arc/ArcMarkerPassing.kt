package arc

import de.kimanufaktur.markerpassing.Link
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.edges.EdgeType
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing
import de.kimanufaktur.nsm.graph.entities.links.AntonymLink
import de.kimanufaktur.nsm.graph.entities.links.DefinitionLink
import de.kimanufaktur.nsm.graph.entities.links.HypernymLink
import de.kimanufaktur.nsm.graph.entities.links.HyponymLink
import de.kimanufaktur.nsm.graph.entities.links.MeronymLink
import de.kimanufaktur.nsm.graph.entities.links.NamedEntityLink
import de.kimanufaktur.nsm.graph.entities.links.SemanticRoleLink
import de.kimanufaktur.nsm.graph.entities.links.SynonymLink
import de.kimanufaktur.nsm.graph.entities.links.SyntaxLink
import de.kimanufaktur.nsm.graph.entities.marker.DoubleMarkerWithOrigin
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds
import org.jgrapht.Graph

class ArcMarkerPassing(
    graph: Graph<Concept, WeightedEdge>,
    threshold: Map<Concept, Double>,
    nodeType: Class<DoubleNodeWithMultipleThresholds>
) : DoubleMarkerPassing(graph, threshold, nodeType) {

    fun doInitialMarking(startActivation: Map<Concept, List<DoubleMarkerWithOrigin>>) {
        doInitialMarking(listOf(startActivation), this)
    }

    private fun WeightedEdge.toLink() = when (this.edgeType) {
        EdgeType.Synonym -> SynonymLink()
        EdgeType.Antonym -> AntonymLink()
        EdgeType.Hyponym -> HyponymLink()
        EdgeType.Hypernym -> HypernymLink()
        EdgeType.Meronym -> MeronymLink()
        EdgeType.Definition -> DefinitionLink()
        EdgeType.Syntax -> SyntaxLink()
        EdgeType.NamedEntity -> NamedEntityLink()
        EdgeType.SemanticRole -> SemanticRoleLink()
        else -> null
    }

    private fun Link.reverseType(edgeType: EdgeType) = when (edgeType) {
        EdgeType.Hyponym -> HypernymLink().reverse()
        EdgeType.Hypernym -> HyponymLink().reverse()
        EdgeType.Definition -> null
        else -> this.reverse()
    }

    private fun Link.reverse() = this.apply {
        this.source = this@reverse.target
        this.target = this@reverse.source
    }


    @Suppress("UNCHECKED_CAST")
    override fun <T : DoubleNodeWithMultipleThresholds?> fillNodes(
        graph: Graph<*, *>?,
        threshold: MutableMap<Concept, Double>?,
        nodeType: Class<T>?) {
        if (graph == null || threshold == null || nodeType != DoubleNodeWithMultipleThresholds::javaClass) return
        fillNodes(
            graph = graph as Graph<Concept, WeightedEdge>,
            thresholdMap = threshold.toMap()
        )
    }

    private fun fillNodes(
        graph: Graph<Concept, WeightedEdge>,
        thresholdMap: Map<Concept, Double>
    ) {
        graph.vertexSet().forEach { sourceConcept ->
            val source = nodes[sourceConcept]
                ?: DoubleNodeWithMultipleThresholds(sourceConcept)
                    .also { it.threshold = thresholdMap }

            graph.edgesOf(sourceConcept).forEach { edge ->
                edge.toLink()?.also { link ->
                    link.source = source
                    val targetConcept = edge.target as Concept
                    val target = nodes[targetConcept]
                        ?: DoubleNodeWithMultipleThresholds(targetConcept)
                            .also { it.threshold = thresholdMap }
                    link.target = target
                    if (link !in source.links) source.addLink(link)
                    link.reverseType(edge.edgeType)?.let { reversedLink ->
                        if (link !in target.links) target.addLink(reversedLink)
                    }
                    nodes[sourceConcept] = source
                    nodes[targetConcept] = target
                }
            }
        }
    }
}