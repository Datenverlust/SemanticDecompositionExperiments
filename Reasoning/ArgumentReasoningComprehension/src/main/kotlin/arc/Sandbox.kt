package arc

import arc.dataset.allElements
import arc.util.merge
import arc.util.printSize
import arc.util.saveToFile
import arc.util.userHome
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.runBlocking
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

//    val graphBuilder = ParallelGraphBuilder(3)
//    val parallelGraph = graphBuilder.startAsync(arcTask.allElements()).map { it.graph }.merge()

//
    val component = "Amazon does not allow more leeway and money to the writers.".toGraphComponent()
//    val component = arcTask.reason.toGraphComponent()
    val startTime = System.currentTimeMillis()
    val graph = arcTask.allElements().map { it.toGraphComponent().graph }.merge()
    val endTime = System.currentTimeMillis()
    val duration = endTime - startTime
    println("duration: $duration ms")
    graph.printSize()

    graph.saveToFile(File(userHome("Dokumente/graph"), "Tipping_Full.graphml"))

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