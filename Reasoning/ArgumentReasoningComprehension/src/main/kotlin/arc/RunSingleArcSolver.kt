package arc

import arc.dataset.Dataset
import arc.util.printProgress
import arc.dataset.readDataset
import arc.util.getIdsOfDoneTasks
import arc.util.print
import arc.util.saveResult

fun main() {
    val bulkSize = 10
    val solver = ArcSolver()
    readDataset(Dataset.ADVERSIAL_TEST)?.let { dataSet ->
        var notDone = true
        while (notDone) {
            val tasksDone = getIdsOfDoneTasks()
            println("Done Tasks: ${tasksDone.size}")
            if (tasksDone.size == dataSet.size) notDone = false
            dataSet.asSequence()
                .filterNot { it.id in tasksDone }
                .take(bulkSize)
                .printProgress(1, bulkSize)
                .map { task -> solver.invoke(task).also { saveResult(it, ArcGraphConfig().hashCode().toString()) } }
                .toList().print()

        }
    }
}