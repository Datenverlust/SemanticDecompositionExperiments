package arc

import arc.dataset.allTextElements

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
//    val component = "Amazon does not allow more leeway and money to the writers.".toGraphComponent()
//    val component = arcTask.reason.toGraphComponent()
//    val startTime = System.currentTimeMillis()
//    val graph = arcTask.allElements().map { it.toGraphComponent().graph }.merge()
//    val endTime = System.currentTimeMillis()
//    val duration = endTime - startTime
//    println("duration: $duration ms")
//    graph.printSize()
//
//    graph.saveToFile(File(userHome("Dokumente/graph"), "Tipping_Full.graphml"))
//
//    println("debug")
}