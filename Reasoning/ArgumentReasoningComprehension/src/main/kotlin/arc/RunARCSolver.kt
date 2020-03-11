package arc

import arc.utils.Dataset
import arc.utils.readDataset

fun main() {
    ARCSolver().let { solver ->
        readDataset(Dataset.ADVERSIAL_TRAIN)?.let { dataset ->
            dataset
                .map { task ->
                    task to solver.invoke(task)
                }
                .forEach { (task, result) ->

                }
        }
    }
}