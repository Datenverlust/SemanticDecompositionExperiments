package arc.util

import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import org.jgrapht.graph.DefaultListenableGraph

fun DefaultListenableGraph<String, WeightedEdge>.printSize() {
    println("#vertices: ${vertexSet().size}\n#edges: ${edgeSet().size}")
}