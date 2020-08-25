package arc

import arc.dataset.Dataset
import arc.dataset.readDataset
import arc.util.getIdsOfDoneTasks
import arc.util.print
import arc.util.saveResult

fun main() {

    val parallelArcSolver = ParallelArcSolver(
        numThreads = 5,
        arcSolverFactory = { ArcSolver() }
    )
    while (true) {
        val tasksDone = getIdsOfDoneTasks()
        println("Done Tasks: ${tasksDone.size}")
        readDataset(Dataset.ADVERSIAL_TEST)
            ?.filterNot { it.id in tasksDone }
            ?.take(10)
            ?.let { test -> parallelArcSolver.startAsync(test) }
            ?.also { it.print() }
            ?.forEach { saveResult(it, "") }
    }
}