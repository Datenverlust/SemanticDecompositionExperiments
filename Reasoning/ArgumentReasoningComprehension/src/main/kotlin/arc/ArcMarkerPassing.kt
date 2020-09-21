package arc

import arc.util.reverseByType
import arc.util.toEmptyLink
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig
import de.kimanufaktur.nsm.graph.entities.marker.DoubleMarkerWithOrigin
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds
import org.jgrapht.Graph

var currentConfig: ArcConfig = ArcConfig()

class ArcMarkerPassing(
    graph: Graph<Concept, WeightedEdge>,
    threshold: Map<Concept, Double>,
    nodeType: Class<DoubleNodeWithMultipleThresholds>
) : DoubleMarkerPassing(graph, threshold, nodeType) {

    fun doInitialMarking(startActivation: Map<Concept, List<DoubleMarkerWithOrigin>>) {
        doInitialMarking(listOf(startActivation), this)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : DoubleNodeWithMultipleThresholds?> fillNodes(
        graph: Graph<*, *>?,
        threshold: MutableMap<Concept, Double>?,
        nodeType: Class<T>?) {
        //TODO: check nodeType
        MarkerPassingConfig.setDoubleActivationLimit(Double.POSITIVE_INFINITY)
        MarkerPassingConfig.setTerminationPulsCount(99)
        if (graph == null || threshold == null) return
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
            val sourceNode = nodes[sourceConcept]
                ?: DoubleNodeWithMultipleThresholds(sourceConcept)
                    .also { it.threshold = thresholdMap }
                    .also { nodes[sourceConcept] = it }

            graph.edgeSet().filter { edge -> edge.source as Concept == sourceConcept }
                .forEach { edge ->
                    edge.toEmptyLink(currentConfig)?.also { link ->
                        link.source = sourceNode
                        val targetConcept = edge.target as Concept
                        val targetNode = nodes[targetConcept]
                            ?: DoubleNodeWithMultipleThresholds(targetConcept)
                                .also { it.threshold = thresholdMap }
                        link.target = targetNode
                        if (link !in sourceNode.links) sourceNode.addLink(link)
                        link.reverseByType(edge.edgeType, currentConfig)?.let { reversedLink ->
                            if (link !in targetNode.links) targetNode.addLink(reversedLink)
                        }
                        nodes[sourceConcept] = sourceNode
                        nodes[targetConcept] = targetNode
                    }
                }
        }
    }
}