package arc

import arc.dataset.allElements
import arc.util.merge
import arc.util.userHome
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
import org.jgrapht.io.Attribute
import org.jgrapht.io.AttributeType
import org.jgrapht.io.DefaultAttribute
import org.jgrapht.io.GraphMLExporter
import java.io.File
import java.util.Collections

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

//    val wsdSentence = "I am visiting my mother today. The Mediterranean was mother to many cultures and languages."
    //    val sentence = arcTask.allElements().map { it.toGraphComponent().graph}.merge()
//    val init = arcTask.warrant0.toGraphComponent()
//    graphCache.clear()
//
//
//    val startTimeParallel = System.currentTimeMillis()
//    val graphBuilder = ParallelGraphBuilder(3)
//    val parallelGraph = graphBuilder.startAsync(arcTask.allElements()).map { it.graph }.merge()
//    val endTimeParallel = System.currentTimeMillis()
//    val durationParallel = endTimeParallel - startTimeParallel
//    println("parallel duration: $durationParallel ms")
//    graphCache.clear()
//    "mother".decompose(ArcGraphConfig(depth = 1),"NN")
//
    val component = "Amazon does not allow more leeway and money to the writers.".toGraphComponent()

//    val startTime = System.currentTimeMillis()
//    val con = arcTask.allElements().map { it.toGraphComponent().graph}.merge()
////    val con = arcTask.reason.toGraphComponent(ArcGraphConfig()).graph
//    val endTime = System.currentTimeMillis()
//    val duration = (endTime - startTime).toDouble() / 1000.0
//    println("duration: $duration s")


    val exporter = GraphMLExporter<Concept, WeightedEdge>(
        { concept -> concept.hashCode().toString() },
        { concept -> concept.litheral + ": " + concept.assignedSenseKeys.toString() },
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
        { edge -> mapOf() }
    )
    val outputWriter = File(userHome("Dokumente/graph"), "Amazon.graphml").writer()
    exporter.exportGraph(component.graph, outputWriter)

    println("debug")
}

class ParallelGraphBuilder(
    val numThreads: Int
) {
    fun startAsync(components: List<String>): List<GraphComponent> = runBlocking {
        val componentChannel = Channel<String>()
        val graphs = Collections.synchronizedList(ArrayList<GraphComponent>(components.size))
        val runners = (1..numThreads).map {
            async(Dispatchers.IO) {
                buildGraphs(componentChannel, graphs)
            }
        }
        components.forEach { componentChannel.send(it) }
        componentChannel.close()
        runners.forEach { it.await() }
        return@runBlocking graphs
    }

    internal suspend fun buildGraphs(componentChannel: Channel<String>, graphs: MutableList<GraphComponent>) {
        for (component in componentChannel) {
            val graph = component.toGraphComponent()
            graphs.add(graph)
        }
    }
}