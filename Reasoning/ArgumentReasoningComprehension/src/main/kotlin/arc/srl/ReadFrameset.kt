package arc.srl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

val framesDir = {}::class.java.getResource("").path
    .replace("target/test-classes", "src/main/resources")
    .let { path -> File(path, "frames") }

private val kotlinXmlMapper = XmlMapper(
    JacksonXmlModule().apply {
        setDefaultUseWrapper(false)
    }
)
    .enable(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES)
    .enable(DeserializationFeature.ACCEPT_EMPTY_STRING_AS_NULL_OBJECT)
    .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    .enable(DeserializationFeature.ACCEPT_EMPTY_ARRAY_AS_NULL_OBJECT)
    .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
    .registerKotlinModule()


private fun Frameset.asPredicatesMap() =
    predicate?.mapNotNull { p -> p.lemma?.let { it to p } }?.toMap()

private fun Frameset.getRolesForAllSenses(lemma: String) =
    asPredicatesMap()?.get(lemma)?.roleset
        ?.mapNotNull { roleSet ->
            roleSet.id?.let { id ->
                roleSet.roles?.role
                    ?.map { it.descr }
                    ?.let { roles -> id to roles }
            }
        }
        ?.toMap()

fun getFrameset(lemma: String) = File(framesDir, "${lemma.substringBefore(" ")}.xml")
    .let { frameFile ->
        if (frameFile.exists()) {
            kotlinXmlMapper.readValue<Frameset>(frameFile.readText())
        } else null
    }

fun getRoleSetMap(lemma: String) = getFrameset(lemma)?.getRolesForAllSenses(lemma)

fun getRoleSet(lemma: String, sense: String) = getRoleSetMap(lemma)?.get(sense)