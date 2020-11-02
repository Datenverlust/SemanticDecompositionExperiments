@file:Suppress("RECEIVER_NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")

package arc.util

import arc.ArcLabel
import arc.ArcResult
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import java.io.File

val resultsDir = File(userHome("Dokumente"), "arc_results").also { it.mkdirs() }

fun saveResult(result: ArcResult, folderName: String) = File(resultsDir, folderName)
    .also { it.mkdirs() }
    .let { dir ->
        File(dir, "${result.id}.json").writeText(ObjectMapper().writeValueAsString(result))
    }

fun getIdsOfDoneTasks(dirName: String) = File(resultsDir, dirName)
    .also { it.mkdirs() }
    .listFiles()
    .map { it.name.replace(".json", "") }

fun List<ArcResult>.print() {
    filter { it.foundLabel == it.correctLabel }
        .let { successfulTasks ->
            "%s(${successfulTasks.size}) von $size Datenpunkten richtig gelabelt".format(
                String.format("%.2f%%", (successfulTasks.size.toDouble() / size) * 100)
            ).let { println(it) }
        }
    filter { it.foundLabel != it.correctLabel && it.foundLabel != ArcLabel.UNKNOWN }
        .let { failedTasks ->
            "%s(${failedTasks.size}) von $size Datenpunkten falsch gelabelt".format(
                String.format("%.2f%%", (failedTasks.size.toDouble() / size) * 100)
            ).let { println(it) }
        }
    filter { it.foundLabel == ArcLabel.UNKNOWN }
        .let { notProceededTasks ->
            "%s(${notProceededTasks.size}) von $size Datenpunkten nicht gelabelt".format(
                String.format("%.2f%%", (notProceededTasks.size.toDouble() / size) * 100)
            ).let { println(it) }
        }
}

fun evaluateResults(dirName: String) = File(resultsDir, dirName).listFiles()
    .map { file -> file.parseAsArcResult() }

fun File.parseAsArcResult(): ArcResult = ObjectMapper().readValue(this)