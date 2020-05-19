package arc.wsd

import khttp.post

typealias WSDResponse = List<Pair<WordSense, Double>>

data class WSDRequest(
    val markedContext: String,
    val wordSenses: List<WordSense>
)

data class WordSense(
    val gloss: String,
    val senseKey: String
)

class WSDClient(
    val host: String = "localhost",
    val port: String = "5000",
    private val clientThreshold: Double = 0.5
) {
    init {
        System.err.close()
        System.setErr(System.out)
    }

    fun disambiguate(requests: List<WSDRequest>, threshold: Double = clientThreshold) =
        sendRequest(
            requests.joinToString("\n") { it.convertToCsvString() },
            requests.map { request -> request.wordSenses }.flatten()
        )
            .let { it?.cut(requests.map { request -> request.wordSenses.size }) }
            ?.map { response ->
                response
                    .filter { (_, score) -> score >= threshold }
                    .map { (sense, _) -> sense }
            }

    fun disambiguate(request: WSDRequest, threshold: Double = clientThreshold) =
        sendRequest(request.convertToCsvString(), request.wordSenses)
            ?.filter { (_, score) -> score >= threshold }
            ?.map { (sense, _) -> sense }

    private fun sendRequest(rawRequest: String, wordSenses: List<WordSense>): WSDResponse? {
        return post(url = "http://$host:$port", data = rawRequest)
            .text
            .split("\n")
            .map { it.split("\t") }
            .also { lines -> if (!lines.all { it.size == 3 }) return null }
            .map { it[2].toDouble() }
            .let { wordSenses.zip(it) }
    }

    private fun WSDRequest.convertToCsvString() =
        this.wordSenses.joinToString("\n") {
            listOf(
                "id",
                "0",
                this.markedContext,
                it.gloss,
                it.senseKey
            ).joinToString("\t")
        }

    internal fun WSDResponse.cut(sizes: List<Int>): List<WSDResponse> {
        var responseList = this
        return sizes.map {
            val thisResponses = responseList.take(it)
            responseList = responseList.drop(it)
            thisResponses
        }
    }
}
