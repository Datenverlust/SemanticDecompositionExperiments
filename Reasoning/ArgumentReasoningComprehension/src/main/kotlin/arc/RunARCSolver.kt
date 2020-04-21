package arc

import arc.util.Dataset
import arc.util.printProgress
import arc.util.readDataset

fun main() {
    ARCSolver().let { solver ->
        readDataset(Dataset.ADVERSIAL_TRAIN)?.let { dataset ->
            val sizeToTake = 10
            dataset.asSequence()
                .take(sizeToTake)
                .printProgress(1, sizeToTake)
                .mapIndexed { index, task ->
                    val solution = solver.invoke(task)
                    if (task.correctLabelW0orW1 == solution) 1.0
                    else 0.0
                }
                .average()
                .let { result ->
                    "%s richtig gelabelt".format(
                        String.format("%.2f%%", result * 100)
                    ).let { println(it) }
                }
        }
    }
}