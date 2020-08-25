package arc.util

import arc.ArcLabel
import arc.ArcResult
import java.io.File

val resultsDir = File(userHome("Dokumente"), "arc_results").also { it.mkdirs() }

fun ArcResult.toText() = listOf(
    "found label: ${foundLabel}",
    "correct label: ${correctLabel}",
    "w0 score: ${resultW0.score}",
    "\t#vertices: ${resultW0.numVertices}",
    "\t#edges: ${resultW0.numEdges}",
    "w1 score: ${resultW1.score}",
    "\t#vertices: ${resultW1.numVertices}",
    "\t#edges: ${resultW1.numEdges}"
)
    .joinToString("\n")

fun saveResult(result: ArcResult, folderName: String) = File(resultsDir, folderName)
    .also { it.mkdirs() }
    .let { dir ->
        File(dir, "${result.id}.txt").writeText(result.toText())
    }

fun getIdsOfDoneTasks(dirName: String) = File(resultsDir, dirName).listFiles().map { it.name.replace(".txt", "") }

fun List<ArcResult>.print() {
    filter { it.foundLabel == it.correctLabel }
        .let { successfulTasks ->
            "%s von $size Datenpunkten richtig gelabelt".format(
                String.format("%.2f%%", (successfulTasks.size.toDouble() / size) * 100)
            ).let { println(it) }
        }
    filter { it.foundLabel != it.correctLabel && it.foundLabel != ArcLabel.UNKNOWN }
        .let { failedTasks ->
            "%s von $size Datenpunkten falsch gelabelt".format(
                String.format("%.2f%%", (failedTasks.size.toDouble() / size) * 100)
            ).let { println(it) }
        }
    filter { it.foundLabel == ArcLabel.UNKNOWN }
        .let { notProceededTasks ->
            "%s von $size Datenpunkten nicht gelabelt".format(
                String.format("%.2f%%", (notProceededTasks.size.toDouble() / size) * 100)
            ).let { println(it) }
        }
}