package arc

import arc.util.createEdge
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.edges.EdgeType
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig
import de.kimanufaktur.nsm.graph.entities.marker.DoubleMarkerWithOrigin
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultListenableGraph

fun main() {
    val arcTask = ArcTask(
        id = "9309004_185_AE861G0AY5RGT",
        warrant0 = "most waiters do work hard",
        warrant1 = "most waiters do no work hard",
        correctLabelW0orW1 = ArcLabel.W0,
        reason = "Tipping rewards entrepreneurial spirit and hard work.",
        claim = "To tip",
        debateTitle = "To Tip or Not to Tip",
        debateInfo = "Should restaurants do away with tipping?"
    )
    val graph = DefaultListenableGraph(DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java))
    val concepts = listOf("one", "two", "three", "four", "five", "six", "seven", "eight", "nine", "ten").map { Concept(it) }
    concepts.forEach { graph.addVertex(it) }
    concepts.zipWithNext().forEach { (first, second) ->
        graph.addEdge(first, second, createEdge(EdgeType.Synonym, first, second))
    }
    val startActivation = listOf(concepts.first(), concepts.last()).map {
        it to listOf(
            DoubleMarkerWithOrigin()
                .apply {
                    origin = it
                    activation = 100.0
                }
        )
    }.toMap()
    val markerPassing = ArcMarkerPassing(
        graph = graph,
        threshold = concepts.map { it to -1.0 }.toMap(),
        nodeType = DoubleNodeWithMultipleThresholds::class.java
    )

    MarkerPassingConfig.setDoubleActivationLimit(Double.POSITIVE_INFINITY)
    MarkerPassingConfig.setTerminationPulsCount(10)
    markerPassing.doInitialMarking(startActivation)

    markerPassing.execute()
    println("debug")

//    val solver = ArcSolver()
//    val graph = readDataset(Dataset.ADVERSIAL_TEST)?.first()?.allTextElements()?.map { solver.createGraphData(it).graph }?.merge()
//    graph?.saveToFile(File(userHome("Dokumente/graph"), "First_Tryout_${ArcConfig().hashCode()}.graphml"))
//    val results = evaluateResults(ArcConfig().toDirName())//.filterNot { it.id.contains("adversarial") }
//    println(results.filter { it.correctLabel == it.foundLabel }.size.toDouble() / results.size)
//    println("debug")
}