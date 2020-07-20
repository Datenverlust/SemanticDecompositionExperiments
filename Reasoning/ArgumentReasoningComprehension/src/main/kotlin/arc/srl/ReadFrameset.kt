package arc.srl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

private val framesDir = {}::class.java.getResource("").path
    .replace("target/test-classes", "src/main/resources")
    .let { path -> File(path, "frames") }

private val kotlinXmlMapper = XmlMapper(
    JacksonXmlModule().apply {
        setDefaultUseWrapper(false)
    }
)
    .registerKotlinModule()
    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

private fun Frameset.asPredicatesMap() =
    predicate?.mapNotNull { p -> p.lemma?.let { it to p } }?.toMap()

private fun Frameset.getRolesetMap(lemma: String) =
    asPredicatesMap()?.get(lemma)?.roleset
        ?.mapNotNull { roleset ->
            roleset.id?.let { id ->
                roleset.roles?.role
                    ?.map { it.descr }
                    ?.let { roles -> id to roles }
            }
        }
        ?.toMap()

fun getRoleSet(lemma: String, sense: String) = File(framesDir, "$lemma.xml")
    .readText()
    .let { kotlinXmlMapper.readValue<Frameset>(it).getRolesetMap(lemma)?.get(sense) }
