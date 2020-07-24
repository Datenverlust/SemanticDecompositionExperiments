package arc.dataset

import arc.ArcTask
import arc.ArcLabel
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
    Dataset.ADVERSIAL_DEV to "adversarial/dev-adv-negated.csv",
    Dataset.ADVERSIAL_TRAIN to "adversarial/train-adv-negated.csv",
    Dataset.ADVERSIAL_TEST to "adversarial/test-adv-negated.csv",
    Dataset.SEMEVAL_DEV to "semeval2018/dev-full.txt",
    Dataset.SEMEVAL_TRAIN to "semeval2018/train-full.txt",
    Dataset.SEMEVAL_TEST to "semeval2018/test-full.txt",
    Dataset.SEMEVAL_SWAPPED to "semeval2018/train-w-swap-full.txt"
)

internal val resourcesDir = {}::class.java.getResource("").path

fun readDataset(dataset: Dataset) =
    datasetFilesMap[dataset]?.let { datasetPath ->
        File(resourcesDir, datasetPath).useLines { lines ->
            lines
                .filterNot { it.startsWith("#") }
                .filter { it.isNotEmpty() }
                .map { it.split("\t") }
                .filter { it.size >= 8 }
                .map {
                    if (dataset == Dataset.ADVERSIAL_TEST) {
                        ArcTask(
                            id = it[0],
                            warrant0 = it[1],
                            warrant1 = it[2],
                            correctLabelW0orW1 = if (it[7] == "0") ArcLabel.W0 else if (it[7] == "1") ArcLabel.W1 else ArcLabel.UNKNOWN,
                            reason = it[3],
                            claim = it[4],
                            debateTitle = it[5],
                            debateInfo = it[6]
                        )
                    } else {
                        ArcTask(
                            id = it[0],
                            warrant0 = it[1],
                            warrant1 = it[2],
                            correctLabelW0orW1 = if (it[3] == "0") ArcLabel.W0 else if (it[3] == "1") ArcLabel.W1 else ArcLabel.UNKNOWN,
                            reason = it[4],
                            claim = it[5],
                            debateTitle = it[6],
                            debateInfo = it[7]
                        )
                    }
                }
                .toList()
        }
    }

fun ArcTask.allElements() =
    listOf(this.warrant0, this.warrant1, this.reason, this.claim, this.debateTitle, this.debateInfo)