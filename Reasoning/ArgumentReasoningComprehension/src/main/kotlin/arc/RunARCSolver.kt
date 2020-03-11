package arc

import arc.utils.Dataset
import arc.utils.readDataset

fun main() {
    ARCSolver().let { solver ->
        readDataset(Dataset.ADVERSIAL_TRAIN)?.let { dataset ->
            dataset.filter { it.correctLabelW0orW1 == solver.invoke(it) }
                .let { (it.size.toDouble() / dataset.size.toDouble()) * 100 }
                .let { println("$it  % richtig gelabelt") }
        }
    }
}