package arc

import arc.util.exporter
import arc.util.importer
import com.fasterxml.jackson.databind.ObjectMapper
import de.kimanufaktur.nsm.decomposition.graph.edges.SynonymEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultListenableGraph
import java.io.InputStream
import java.io.ObjectInputStream
import java.io.OutputStream
import java.io.StringReader
import java.io.StringWriter

fun main() {

    val solver = ArcSolver()

//    val d1 = 0.1
//    val d2 = 0.2
//    println(d1 + d1 + d2 + d2)
//    println(d1 + d2 + d2 + d1)
//    val dataSet = readDataset(Dataset.ADVERSIAL_TEST)!![103]
//    val results = (1..10).map {
//        val solver = ArcSolver()
//        solver.invoke(dataSet, ArcConfig())
//    }
//    val graph = DefaultListenableGraph(DefaultDirectedWeightedGraph<String, WeightedEdge>(WeightedEdge::class.java))
//    val nodes = (1..10).map { "node#$it" }
//    nodes.forEach { node -> graph.addVertex(node) }
//    nodes.zipWithNext().forEach { (first, second) ->
//        val edge = SynonymEdge()
//        edge.source = first
//        edge.target = second
//        graph.addEdge(first, second, edge)
//    }
//    val markerHistory = (1..10).map {
//        ArcMarkerPassing(graph, nodes.map { it to 0.1 }.toMap(), StringDoubleNodeWithMultipleThresholds::class.java)
//            .also { mp ->
//                mp.doInitialMarking(
//                    nodes.map { node ->
//                        node to
//                            listOf(StringDoubleMarkerWithOrigin()
//                                .also { marker ->
//                                    marker.origin = node
//                                    marker.activation = 100.0
//                                }
//                            )
//                    }
//                        .toMap()
//                )
////                MarkerPassingConfig.setTerminationPulsCount(3)
//                mp.execute()
//            }
//            .nodes.map { (_, node) -> (node as StringDoubleNodeWithMultipleThresholds).let { thisNode -> thisNode.activationHistory.map { marker -> thisNode.name to marker } } }.flatten()
//            .map { (node, marker)-> node to (marker as StringDoubleMarkerWithOrigin).let { it.origin to it.activation } }
//    }
////        .distinct()
//        .let {
//            val intersection = it[0].intersect(it[1])
//            it.map { markers ->
//                markers.filterNot { it in intersection }
//            }
//        }
//    println(markerHistory.size)
//    val geno = File(userHome("Dokumente/generations_arc"), "test.json").readText().let { ObjectMapper().registerModule(KotlinModule()).readValue<Genotype>(it) }
//    val arcTask = ArcTask(
//        id = "9309004_185_AE861G0AY5RGT",
//        warrant0 = "most waiters do work hard",
//        warrant1 = "most waiters do no work hard",
//        correctLabelW0orW1 = ArcLabel.W0,
//        reason = "Tipping rewards entrepreneurial spirit and hard work.",
//        claim = "To tip",
//        debateTitle = "To Tip or Not to Tip",
//        debateInfo = "Should restaurants do away with tipping?"
//    )
//    val graph = DefaultListenableGraph(DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java))
//    val concepts = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten").map { Concept(it) }
//    concepts.forEach { graph.addVertex(it) }
//    concepts.zipWithNext().forEach { (first, second) ->
//        graph.addEdge(first, second, createEdge(EdgeType.Synonym, first, second))
//    }
//    val startActivation = listOf(concepts.first(), concepts.last()).map {
//        it to listOf(
//            DoubleMarkerWithOrigin()
//                .apply {
//                    origin = it
//                    activation = 100.0
//                }
//        )
//    }.toMap()
//    val markerPassing = ArcMarkerPassing(
//        graph = graph,
//        threshold = concepts.map { it to -1.0 }.toMap(),
//        nodeType = DoubleNodeWithMultipleThresholds::class.java
//    )
//
//    MarkerPassingConfig.setDoubleActivationLimit(Double.POSITIVE_INFINITY)
//    MarkerPassingConfig.setTerminationPulsCount(10)
//    markerPassing.doInitialMarking(startActivation)
//
//    markerPassing.execute()
    println("debug")

//    val solver = ArcSolver()
//    val graph = readDataset(Dataset.ADVERSIAL_TEST)?.first()?.allTextElements()?.map { solver.createGraphData(it).graph }?.merge()
//    graph?.saveToFile(File(userHome("Dokumente/graph"), "First_Tryout_${ArcConfig().hashCode()}.graphml"))
//    val results = evaluateResults(ArcConfig().toDirName())//.filterNot { it.id.contains("adversarial") }
//    println(results.filter { it.correctLabel == it.foundLabel }.size.toDouble() / results.size)
//    println("debug")
}