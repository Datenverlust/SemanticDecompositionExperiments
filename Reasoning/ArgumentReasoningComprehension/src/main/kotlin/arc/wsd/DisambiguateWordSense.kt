package arc.wsd

import khttp.post

private const val host: String = "localhost"
private const val port: String = "5000"
private const val defaultThreshold: Double = 0.5

private fun init() {
    System.err.close()
    System.setErr(System.out)
}

fun List<WSDRequest>.send(threshold: Double = defaultThreshold) =
    sendWsdRequest(
        joinToString("\n") { it.convertToCsvString() },
        map { request -> request.wordSenses }.flatten()
    )
        ?.cut(map { request -> request.wordSenses.size })
        ?.map { response ->
            response
                .filter { (_, score) -> score >= threshold }
                .map { (sense, _) -> sense }
        }

fun WSDRequest.send(threshold: Double = defaultThreshold) =
    sendWsdRequest(convertToCsvString(), wordSenses)
        ?.filter { (_, score) -> score >= threshold }
        ?.map { (sense, _) -> sense }

private fun sendWsdRequest(rawRequest: String, wordSenses: List<WordSense>): WSDResponse? {
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
            markedContext,
            it.gloss,
            it.senseKey
        ).joinToString("\t")
    }

private fun WSDResponse.cut(sizes: List<Int>): List<WSDResponse> {
    var responseList = this
    return sizes.map {
        val thisResponses = responseList.take(it)
        responseList = responseList.drop(it)
        thisResponses
    }
}

