package arc

import arc.dataset.allTextElements
import arc.util.evaluateResults
import arc.util.merge
import arc.util.saveToFile
import arc.util.userHome
import java.io.File

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

//    val solver = ArcSolver()
//    val graph = arcTask.allTextElements().map { solver.buildGraphComponent(it).graph }.merge()
//    graph.saveToFile(File(userHome("Dokumente/graph"), "Tipping_${ArcGraphConfig().hashCode()}.graphml"))
    val results = evaluateResults(ArcGraphConfig().hashCode().toString())

    println(results.filter { it.correctLabel == it.foundLabel }.size.toDouble() / results.size)
}