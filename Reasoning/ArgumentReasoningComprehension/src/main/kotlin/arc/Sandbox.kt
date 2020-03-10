package arc

import arc.wsd.WSDClient
import arc.wsd.WSDRequest
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

fun main() {
    {}::class.java.getResourceAsStream("wsd_examples.yml").readBytes().let { bytes ->
        val client = WSDClient()
        ObjectMapper(YAMLFactory()).registerModule(KotlinModule() as Module?)
            .readValue<List<WSDRequest>>(bytes)
            .let { requests -> client.disambiguate(requests)?.let { requests.zip(it) } }
            ?.forEach { (request, senses) ->
                println("sentence: ${request.sentence}")
                println("word to disambiguate: ${request.sentence.split("""\s+""".toRegex())[request.targetIndex]}")
                println("matching sense: ${senses.map{it.gloss+ " " + it.senseKey}.joinToString(" & ")}")
                println()
            }
    }
}