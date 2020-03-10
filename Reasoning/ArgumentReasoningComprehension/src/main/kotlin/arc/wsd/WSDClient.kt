package arc.wsd

import khttp.post

typealias WSDResponse = List<Pair<WordSense, Double>>

data class WSDRequest(
    val targetIndex: Int,
    val sentence: String,
    val wordSenses: List<WordSense>
)

data class WordSense(
    val gloss: String,
    val senseKey: String
)

class WSDClient(
    val host: String = "localhost",
    val port: String = "5000"
) {
    fun disambiguate(request: WSDRequest, threshold: Double = 0.5) =
        post(url = "http://$host:$port", data = request.convertToCsvString()).text
            .let { readResponse(it, request) }
            ?.filter { (_, score) -> score >= threshold }
            ?.map { (sense, _) -> sense }

    internal fun readResponse(rawResponse: String, request: WSDRequest): WSDResponse? {
        return rawResponse.split("\n")
            .map { it.split("\t") }
            .also { lines -> if (!lines.all { it.size == 3 }) return null }
            .map { it[2].toDouble() }
            .let { request.wordSenses.zip(it) }
    }

    internal fun WSDRequest.convertToCsvString() =
        this.wordSenses.map {
            listOf(
                "id",
                "?",
                this.sentence,
                it.gloss,
                it.senseKey
            ).joinToString("\t")
        }.joinToString("\n")
}
