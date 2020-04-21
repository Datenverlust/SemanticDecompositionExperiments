package arc

import com.fasterxml.jackson.dataformat.xml.XmlMapper
import com.fasterxml.jackson.module.kotlin.readValue
import de.kimanufaktur.nsm.decomposition.Decomposition

data class Wrapper(
    val id: String
)
fun main() {
    val xmlMapped = "<wrapper id=\"identification\"></wrapper>".let { XmlMapper().readValue<Wrapper>(it) }
    println("nothing")
}