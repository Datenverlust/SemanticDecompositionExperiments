package arc

import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge

data class EdgeInfos(
    val edgeClass: Class<Any>,
    val edgeWeight: Double
)

val edgeMapping = NotImplementedError().also { throw it }