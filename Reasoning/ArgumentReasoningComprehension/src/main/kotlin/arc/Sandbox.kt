package arc

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.Decomposition
import de.kimanufaktur.nsm.decomposition.graph.edges.SynonymEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import edu.stanford.nlp.pipeline.CoreDocument
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultListenableGraph

data class Wrapper(
    val id: String
)

fun main() {
//    val arcSolver = ARCSolver()
//    val pipeline = arcSolver.syntaxPipeline
//    val doc = "Maria is not my teacher. I do not know anything about Russia. Who do you think you are?"
//        .let { CoreDocument(it).also { pipeline.annotate(it) } }
//        .sentences().map { sentence -> sentence.tokens().map { token -> token.sentIndex() to token.ner() } }
//    println("debug")

    val graph = DefaultListenableGraph(
        DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
    ).also {
        val source = Concept("nose")
        val target = Concept("cold")
        it.addVertex(source)
        it.addVertex(target)
        val edge = SynonymEdge()
        edge.source = source
        edge.target = target
        it.addEdge(source,target,edge)
        println("debug")
    }
}