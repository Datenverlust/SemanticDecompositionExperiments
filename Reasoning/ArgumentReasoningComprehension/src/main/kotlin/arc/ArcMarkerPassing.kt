package arc

import arc.util.reverseByType
import arc.util.toEmptyLink
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.StringDoubleMarkerPassing
import de.kimanufaktur.nsm.graph.entities.marker.StringDoubleMarkerWithOrigin
import de.kimanufaktur.nsm.graph.entities.nodes.StringDoubleNodeWithMultipleThresholds
import org.jgrapht.Graph

var currentConfig: ArcConfig = ArcConfig()

class ArcMarkerPassing(
    graph: Graph<String, WeightedEdge>,
    threshold: Map<String, Double>,
    nodeType: Class<StringDoubleNodeWithMultipleThresholds>
) : StringDoubleMarkerPassing(graph, threshold, nodeType) {

    fun doInitialMarking(startActivation: Map<String, List<StringDoubleMarkerWithOrigin>>) {
        doInitialMarking(listOf(startActivation), this)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T : StringDoubleNodeWithMultipleThresholds?> fillNodes(
        graph: Graph<*, *>?,
        threshold: MutableMap<String, Double>?,
        nodeType: Class<T>?) {
        //TODO: check nodeType
        MarkerPassingConfig.setDoubleActivationLimit(Double.POSITIVE_INFINITY)
        MarkerPassingConfig.setTerminationPulsCount(99)
        if (graph == null || threshold == null) return
        fillNodes(
            graph = graph as Graph<String, WeightedEdge>,
            thresholdMap = threshold.toMap()
        )
    }

    private fun fillNodes(
        graph: Graph<String, WeightedEdge>,
        thresholdMap: Map<String, Double>
    ) {
        graph.vertexSet().forEach { sourceConcept ->
            val sourceNode = nodes[sourceConcept]
                ?: StringDoubleNodeWithMultipleThresholds(sourceConcept)
                    .also { it.threshold = thresholdMap }
                    .also { nodes[sourceConcept] = it }

            graph.edgeSet().filter { edge -> edge.source as String == sourceConcept }
                .forEach { edge ->
                    edge.toEmptyLink(currentConfig)?.also { link ->
                        link.source = sourceNode
                        val targetConcept = edge.target as String
                        val targetNode = nodes[targetConcept]
                            ?: StringDoubleNodeWithMultipleThresholds(targetConcept)
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