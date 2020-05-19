package arc

import arc.util.Dataset
import arc.util.printProgress
import arc.util.readDataset

fun main() {
    ARCSolver().let { solver ->
        readDataset(Dataset.ADVERSIAL_TEST)?.let { dataset ->
            val sizeToTake = 100
            dataset.asSequence()
                .take(sizeToTake)
                .printProgress(1, sizeToTake)
                .map { task ->
                    var result = 0.0
                    try {
                        val solution = solver.invoke(task)
                        if (task.correctLabelW0orW1 == solution) result = 1.0
                    } catch (e:Exception) {
                        print(e.message)
                    }
                    result
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