package arc.srl

import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.MapperFeature
import com.fasterxml.jackson.dataformat.xml.JacksonXmlModule
import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import java.io.File

internal val kotlinXmlMapper = XmlMapper(JacksonXmlModule().apply {
    setDefaultUseWrapper(false)
}).registerKotlinModule()
    .configure(MapperFeature.ACCEPT_CASE_INSENSITIVE_PROPERTIES, true)
    .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)

internal data class Frameset(
    val predicate: List<Predicate>? = null
)

internal data class Predicate(
    val lemma: String? = null,
    val roleset: List<Roleset>? = null
)

internal data class Roleset(
    val id: String? = null,
    val name: String? = null,
    val roles: Roles? = null
)

internal data class Roles(
    val role: List<Role>? = null
)

internal data class Role(
    val descr: String? = null,
    val f: String? = null,
    val n: String? = null,
    val vnRole: VnRole? = null
)

internal data class VnRole(
    val vntheta: String? = null
)

internal fun Frameset.asPredicatesMap() =
    this.predicate?.mapNotNull { p -> p.lemma?.let { it to p } }?.toMap()

internal fun Frameset.getRolesetMap(lemma: String) =
    this.asPredicatesMap()?.get(lemma)?.roleset
        ?.mapNotNull { roleset ->
            roleset.id?.let { id ->
                roleset.roles?.role
                    ?.map { it.descr } //TODO: add abstract v-theta-role
                    ?.let { roles -> id to roles }
            }
        }
        ?.toMap()

internal val framesDir = File({}::class.java.getResource("").path, "frames")

fun getRoleset(lemma: String, sense: String) = File(framesDir, "$lemma.xml")
    .readText()
    .let { kotlinXmlMapper.readValue<Frameset>(it).getRolesetMap(lemma)?.get(sense) }
