package arc

import arc.dataset.Dataset
import arc.util.printProgress
import arc.dataset.readDataset

fun main() {
    val sizeToTake = 10
    val results = readDataset(Dataset.ADVERSIAL_TRAIN)?.let { dataSet ->
        dataSet.asSequence()
            .take(sizeToTake)
            .printProgress(1, sizeToTake)
            .map { task -> task to task.solve() }

            .toList()
            .also { results ->
                results.filter { (task, label) -> label == task.correctLabelW0orW1 }
                    .let { successfulTasks ->
                        "%s von $sizeToTake Datenpunkten richtig gelabelt".format(
                            String.format("%.2f%%", (successfulTasks.size.toDouble() / sizeToTake) * 100)
                        ).let { println(it) }
                    }
                results.filter { (task, label) -> label != task.correctLabelW0orW1 && label != ArcLabel.UNKNOWN }
                    .let { failedTasks ->
                        "%s von $sizeToTake Datenpunkten falsch gelabelt".format(
                            String.format("%.2f%%", (failedTasks.size.toDouble() / sizeToTake) * 100)
                        ).let { println(it) }
                    }
                results.filter { (_, label) -> label == ArcLabel.UNKNOWN }
                    .let { notProceededTasks ->
                        "%s von $sizeToTake Datenpunkten nicht gelabelt".format(
                            String.format("%.2f%%", (notProceededTasks.size.toDouble() / sizeToTake) * 100)
                        ).let { println(it) }
                    }
            }
    }
    println("debug")
}