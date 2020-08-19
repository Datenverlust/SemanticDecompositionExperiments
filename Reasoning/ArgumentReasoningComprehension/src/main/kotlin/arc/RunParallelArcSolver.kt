package arc

import arc.dataset.Dataset
import arc.dataset.readDataset

fun main() {
    val parallelArcSolver = ParallelArcSolver(
        numThreads = 5,
        arcSolverFactory = { ArcSolver() }
    )
    readDataset(Dataset.ADVERSIAL_TEST)
        ?.take(10)
        ?.let { test ->
            parallelArcSolver.startAsync(test)
        }
}