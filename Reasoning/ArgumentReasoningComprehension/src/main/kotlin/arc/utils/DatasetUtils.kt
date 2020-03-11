package arc.utils

import arc.ArcTask
import java.io.File

enum class Dataset {
    ADVERSIAL_DEV,
    ADVERSIAL_TRAIN,
    ADVERSIAL_TEST,
    SEMEVAL_DEV,
    SEMEVAL_TRAIN,
    SEMEVAL_TEST,
    SEMEVAL_SWAPPED
}

internal val datasetFilesMap = mapOf(
    Dataset.ADVERSIAL_DEV to "datasets/adversarial/dev-adv-negated.csv",
    Dataset.ADVERSIAL_TRAIN to "datasets/adversarial/train-adv-negated.csv",
    Dataset.ADVERSIAL_TEST to "datasets/adversarial/test-adv-negated.csv",
    Dataset.SEMEVAL_DEV to "datasets/semeval2018/dev-full.txt",
    Dataset.SEMEVAL_TRAIN to "datasets/semeval2018/train-full.txt",
    Dataset.SEMEVAL_TEST to "datasets/semeval2018/test-full.txt",
    Dataset.SEMEVAL_SWAPPED to "datasets/semeval2018/train-w-swap-full.txt"
)

internal val resourcesDir = {}::class.java.getResource("").path.replace("/utils", "")

fun readDataset(dataset: Dataset) =
    datasetFilesMap.get(dataset)?.let { datasetPath ->
        File(resourcesDir, datasetPath).useLines { lines ->
            lines
                .filterNot { it.startsWith("#") }
                .filter { it.isNotEmpty() }
                .map { it.split("\t") }
                .filter { it.size >= 8 }
                .map {
                    ArcTask(
                        id = it[0],
                        warrant0 = it[1],
                        warrant1 = it[2],
                        correctLabelW0orW1 = it[3],
                        reason = it[4],
                        claim = it[5],
                        debateTitle = it[6],
                        debateInfo = it[7]
                    )
                }
                .toList()
        }
    }