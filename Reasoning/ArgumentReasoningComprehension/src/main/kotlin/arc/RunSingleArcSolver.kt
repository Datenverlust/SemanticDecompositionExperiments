package arc

import arc.dataset.Dataset
import arc.dataset.readDataset
import arc.util.getIdsOfDoneTasks
import arc.util.print
import arc.util.printProgress
import arc.util.saveResult

fun main() {
    val bulkSize = 100
    val solver = ArcSolver()
    val config = ArcConfig()
    val dirName = ArcConfig().toDirName()
    readDataset(Dataset.ADVERSIAL_TEST)?.let { dataSet ->
        var notDone = true
        while (notDone) {
            val tasksDone = getIdsOfDoneTasks(dirName)
            println("Done Tasks: ${tasksDone.size}")
            if (tasksDone.size == dataSet.size) notDone = false
            dataSet.asSequence()
                .filterNot { it.id in tasksDone }
                .take(bulkSize)
                .printProgress(10, bulkSize)
                .map { task -> solver.invoke(task, config).also { saveResult(it, dirName) } }
                .toList().print()
        }
    }
}