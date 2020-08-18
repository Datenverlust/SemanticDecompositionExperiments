package arc.util

import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import org.jgrapht.graph.DefaultListenableGraph

fun DefaultListenableGraph<Concept, WeightedEdge>.printSize() {
    println("#vertices: ${vertexSet().size}\n#edges: ${edgeSet().size}")
}