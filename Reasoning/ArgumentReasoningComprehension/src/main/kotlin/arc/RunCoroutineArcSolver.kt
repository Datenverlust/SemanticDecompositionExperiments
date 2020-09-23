package arc

import arc.dataset.Dataset
import arc.dataset.readDataset
import arc.util.getIdsOfDoneTasks
import arc.util.print
import arc.util.saveResult

fun main() {

    val parallelArcSolver = CoroutineArcSolver(
        numCoroutines = 4,
        solver = ArcSolver()
    )
    val dirName = ArcConfig().toDirName()

    val bulkSize = 1
    readDataset(Dataset.ADVERSIAL_TEST)?.let { dataSet ->
        var tasksDone = getIdsOfDoneTasks(dirName)
        while (tasksDone.size < dataSet.size) {
            println("Done Tasks: ${tasksDone.size}")
            dataSet.filterNot { it.id in tasksDone }
                .take(bulkSize)
                .let { test -> parallelArcSolver.startAsync(test, ArcConfig()) }
                .also { if (it.isNotEmpty()) it.print() }
                .forEach { saveResult(it, dirName) }
            tasksDone = getIdsOfDoneTasks(dirName)
        }
    }
}