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
    val port: String = "5000",
    val clientThreshold: Double = 0.5
) {
    init {
        System.err.close();
        System.setErr(System.out);
    }

    fun disambiguate(requests: List<WSDRequest>, threshold: Double = clientThreshold) =
        requests
            .map { it.convertToCsvString() }
            .joinToString("\n")
            .let { sendRequest(it, requests.map { it.wordSenses }.flatten()) }
            .let { it?.cut(requests.map { it.wordSenses.size }) }
            ?.map { response ->
                response
                    .filter { (_, score) -> score >= threshold }
                    .map { (sense, _) -> sense }
            }

    fun disambiguate(request: WSDRequest, threshold: Double = clientThreshold) =
        request.convertToCsvString()
            .let { sendRequest(it, request.wordSenses) }
            ?.filter { (_, score) -> score >= threshold }
            ?.map { (sense, _) -> sense }

    internal fun sendRequest(rawRequest: String, wordSenses: List<WordSense>): WSDResponse? {
        return post(url = "http://$host:$port", data = rawRequest)
            .text
            .split("\n")
            .map { it.split("\t") }
            .also { lines -> if (!lines.all { it.size == 3 }) return null }
            .map { it[2].toDouble() }
            .let { wordSenses.zip(it) }
    }

    internal fun String.markTarget(index: Int) =
        this.split("""\s+""".toRegex()).let {
            it.take(index)
                .plus("\"")
                .plus(it[index])
                .plus("\"")
                .plus(it.drop(index + 1))
                .joinToString(" ")
        }

    internal fun WSDRequest.convertToCsvString() =
        this.wordSenses.map {
            listOf(
                "id",
                "0",
                this.sentence.markTarget(this.targetIndex),
                it.gloss,
                it.senseKey
            ).joinToString("\t")
        }.joinToString("\n")

    internal fun WSDResponse.cut(sizes: List<Int>): List<WSDResponse> {
        var responseList = this
        return sizes.map {
            val thisResponses = responseList.take(it)
            responseList = responseList.drop(it)
            thisResponses
        }
    }
}
