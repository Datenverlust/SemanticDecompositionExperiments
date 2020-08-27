package arc

import arc.dataset.Dataset
import arc.dataset.readDataset
import arc.util.getIdsOfDoneTasks
import arc.util.print
import arc.util.saveResult

fun main() {

    val parallelArcSolver = ParallelArcSolver(
        numCoroutines = 4,
        config = ArcConfig(),
        arcSolverFactory = { ArcSolver() }
    )
    val dirName = ArcConfig().toDirName()

    val bulkSize = 1
    readDataset(Dataset.ADVERSIAL_TEST)?.let { dataSet ->
        var notDone = true
        while (notDone) {
            val tasksDone = getIdsOfDoneTasks(dirName)
            println("Done Tasks: ${tasksDone.size}")
            if (tasksDone.size == dataSet.size) notDone = false
            dataSet.filterNot { it.id in tasksDone }
                .take(bulkSize)
                .let { test -> parallelArcSolver.startAsync(test) }
                .also { if (it.isNotEmpty()) it.print() }
                .forEach { saveResult(it, dirName) }
        }
    }
}