package arc

import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing
import de.kimanufaktur.nsm.graph.entities.marker.DoubleMarkerWithOrigin
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds
import org.jgrapht.Graph


class ArcMarkerPassing(
    graph: Graph<Concept, WeightedEdge>,
    threshold: Map<Concept, Double>,
    nodeType: Class<DoubleNodeWithMultipleThresholds>
) : DoubleMarkerPassing(graph, threshold, nodeType) {

    fun doInitialMarking(startActivation: Map<Concept, List<DoubleMarkerWithOrigin>>) {
        startActivation.let { listOf(it) }
            .let { doInitialMarking(it, this) }
    }
}