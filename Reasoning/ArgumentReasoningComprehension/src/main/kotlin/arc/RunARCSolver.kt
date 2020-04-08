package arc

import arc.utils.Dataset
import arc.utils.readDataset

fun main() {
    ARCSolver().let { solver ->
        readDataset(Dataset.ADVERSIAL_TRAIN)?.let { dataset ->
            dataset
                .take(6)
                .mapIndexed { index, task ->
                    val solution = solver.invoke(task)
                    println("#${index + 1} - ID:${task.id}")
                    if (task.correctLabelW0orW1 == solution) 1.0
                    else 0.0
                }
                .average()
                .let { it * 100 }
                .let { println("$it  % richtig gelabelt") }
        }
    }
}