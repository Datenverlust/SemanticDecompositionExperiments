package arc.wsd

typealias WSDResponse = List<Pair<WordSense, Double>>

data class WSDRequest(
    val markedContext: String,
    val wordSenses: List<WordSense>
)

data class WordSense(
    val senseKey: String,
    val gloss: String
)