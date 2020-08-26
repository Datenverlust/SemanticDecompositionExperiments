package arc

import arc.dataset.Dataset
import arc.dataset.allTextElements
import arc.dataset.readDataset
import arc.negation.findNegationTargets
import arc.util.asAnnotatedCoreDocument
import arc.util.printProgress
import arc.util.syntaxEdges

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
//    val results = evaluateResults(ArcGraphConfig().hashCode().toString())
//
//    println(results.filter { it.correctLabel == it.foundLabel }.size.toDouble() / results.size)
    val negEdges = readDataset(Dataset.ADVERSIAL_TRAIN)
        ?.asSequence()
        ?.printProgress(10)
        ?.map { task ->
            task.allTextElements().map {
                it.asAnnotatedCoreDocument().findNegationTargets()
            }
                .flatten()
        }
        ?.flatten()
        ?.toList()
    println("debug")

}