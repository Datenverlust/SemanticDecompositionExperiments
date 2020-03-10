package arc.arc
import arc.wsd.WSDClient
import arc.wsd.WSDRequest
import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue

fun main() {
    {}::class.java.getResourceAsStream("arc/wsd_examples.yml").readBytes().let { bytes ->
        val client = WSDClient()
        ObjectMapper(YAMLFactory()).registerModule(KotlinModule() as Module?)
            .readValue<List<WSDRequest>>(bytes)
            .forEach { request ->
                println("sentence: ${request.sentence}")
                println("word to disambiguate: ${request.sentence.split("""\s+""".toRegex())[request.targetIndex]}")
                val senseKeyToGlossMap = request.wordSenses.map { it.senseKey to it.gloss }.toMap()
                val response = client.disambiguate(request)
            }
    }
}