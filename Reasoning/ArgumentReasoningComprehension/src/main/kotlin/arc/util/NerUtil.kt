package arc.util

import edu.stanford.nlp.ling.CoreLabel

fun CoreLabel.isNamedEntity() = ner() != null && ner() != "O" && nerConfidence().isReliable(0.5)

private fun Map<String, Double>.isReliable(threshold: Double) =
    get("O")?.let { noNerConfidence -> return noNerConfidence <= threshold }
        ?: toList().first().second > threshold