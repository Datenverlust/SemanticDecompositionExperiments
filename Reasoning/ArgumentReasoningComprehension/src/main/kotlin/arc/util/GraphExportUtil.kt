package arc.util

import de.kimanufaktur.nsm.decomposition.graph.edges.EdgeType
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultListenableGraph
import org.jgrapht.io.GraphMLExporter
import org.jgrapht.io.GraphMLImporter
import java.io.File
import java.io.StringWriter

val exporter = GraphMLExporter<String, WeightedEdge>(
    { name -> name },
    { name -> name },
    { edge -> "${edge.source}_${edge.target}_${edge.edgeType.name}" },
    { edge -> edge.edgeType.name }
)
val importer = GraphMLImporter<String, WeightedEdge>(
    { name, _ -> name.replace("#", "_") },
    { source, target, _, edgeAttributes ->
        edgeAttributes.values.first().value.let { type ->
            createEdge(EdgeType.valueOf(type), source, target)
        }
    }
)

fun DefaultListenableGraph<String, WeightedEdge>.saveToFile(file: File) {
    exporter.exportGraph(this, file.writer())
}

fun writeGraphAsString(graph: DefaultListenableGraph<String, WeightedEdge>) =
    StringWriter()
        .also { exporter.exportGraph(graph, it) }
        .toString()

fun readGraphFromString(graphML: String) =
    DefaultListenableGraph(DefaultDirectedWeightedGraph<String, WeightedEdge>(WeightedEdge::class.java)).also { graph ->
        importer.importGraph(graph, graphML.reader())
    }