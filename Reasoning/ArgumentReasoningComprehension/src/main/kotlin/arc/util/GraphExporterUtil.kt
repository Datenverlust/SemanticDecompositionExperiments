package arc.util

import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import org.jgrapht.graph.DefaultListenableGraph
import org.jgrapht.io.AttributeType
import org.jgrapht.io.DefaultAttribute
import org.jgrapht.io.GraphMLExporter
import java.io.File

val exporter = GraphMLExporter<Concept, WeightedEdge>(
    { concept -> concept.hashCode().toString() },
    { concept ->
        concept.litheral +
            if (concept.assignedSenseKeys.isNotEmpty()) " : ${concept.assignedSenseKeys}" else "" +
                if (concept.negated) " NEG" else ""
    },
    { concept ->
        mapOf(
            "literal" to DefaultAttribute(concept.litheral, AttributeType.STRING),
            "lemma" to DefaultAttribute(concept.lemma, AttributeType.STRING),
            "negated" to DefaultAttribute(concept.negated, AttributeType.BOOLEAN),
            "assignedSenseKeys" to DefaultAttribute(concept.assignedSenseKeys.toString(), AttributeType.STRING),
            "decompositionLevel" to DefaultAttribute(concept.decompositionlevel, AttributeType.INT)
        )
    },
    { edge -> edge.hashCode().toString() },
    { edge -> edge.edgeType.name },
    { mapOf() }
)

fun DefaultListenableGraph<Concept, WeightedEdge>.saveToFile(file: File) {
    exporter.exportGraph(this, file.writer())
}