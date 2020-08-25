package arc.wsd

import arc.util.copy
import de.kimanufaktur.nsm.decomposition.Concept
import khttp.post

private const val host: String = "localhost"
private const val port: String = "5000"
private const val defaultThreshold: Double = 0.5

fun WSDRequest.process(threshold: Double = defaultThreshold) =
    sendWsdRequest(convertToCsvString(), wordSenses)
        ?.let { results ->
            results
                .filter { (_, score) -> score >= threshold }
                .ifEmpty { listOfNotNull(results.maxBy { (_, score) -> score }) }
                .map { (sense, _) -> sense }
        }

private fun sendWsdRequest(rawRequest: String, wordSenses: List<WordSense>): WSDResponse? {
    return post(url = "http://$host:$port", data = rawRequest, timeout = 60.0)
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

fun Concept.disambiguateBy(markedContext: String): Concept {
    if (definitions.isEmpty() || definitions.size == 1) return this
    val wordSenses = definitions.map { def ->
        WordSense(
            senseKey = def.sensekey,
            gloss = def.gloss
        )
    }
    return WSDRequest(
        markedContext = markedContext,
        wordSenses = wordSenses
    )
        .process()
        ?.map { wordSense -> wordSense.senseKey }
        ?.toSet()
        ?.let { senseKeys -> copy().also { it.assignedSenseKeys = senseKeys } }
        ?: this
}

fun Int.markContext(contextAsWords: List<String>) = contextAsWords.asSequence()
    .take(this)
    .plus("\"")
    .plus(contextAsWords[this])
    .plus("\"")
    .plus(contextAsWords.drop(this + 1))
    .joinToString(" ")

