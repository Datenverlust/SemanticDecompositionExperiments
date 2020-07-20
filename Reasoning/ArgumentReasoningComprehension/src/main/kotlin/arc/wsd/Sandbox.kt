package arc.wsd

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

fun main() {
    {}::class.java.getResourceAsStream("wsd_examples.yml").readBytes().let { bytes ->
        ObjectMapper(YAMLFactory()).registerModule(KotlinModule() as Module?)
            .readValue<List<WSDRequest>>(bytes)
            .let { requests -> disambiguate(requests)?.let { requests.zip(it) } }
            ?.forEach { (request, senses) ->
                println("sentence: ${request.markedContext}")
                println("word to disambiguate: ${
                request.markedContext
                    .substringAfter("\"")
                    .substringBefore("\"")
                    .replace("""\s""".toRegex(), "")
                }")
                println("matching sense: ${senses.map { it.gloss + " - " + it.senseKey }.joinToString(" & ")}")
                println()
            }
    }
}