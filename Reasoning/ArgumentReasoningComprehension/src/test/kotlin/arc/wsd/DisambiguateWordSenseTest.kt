package arc.wsd

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.fasterxml.jackson.module.kotlin.readValue
import org.junit.Assert
import org.junit.Test

class DisambiguateWordSenseTest {
    @Test
    fun disambiguateTest() {
        {}::class.java.getResourceAsStream("wsd_examples.yml").readBytes().let { bytes ->
            ObjectMapper(YAMLFactory()).registerModule(KotlinModule())
                .readValue<List<WSDRequest>>(bytes)
                .mapNotNull { request -> request.process()?.let { response -> request to response } }
                .forEach { (request, senseList) ->
                    Assert.assertTrue(senseList.all { sense -> sense in request.wordSenses })
                    Assert.assertTrue(senseList.isNotEmpty())
                }
        }
    }
}