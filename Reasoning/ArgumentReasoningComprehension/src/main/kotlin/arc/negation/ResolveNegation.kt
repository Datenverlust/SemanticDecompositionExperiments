package arc.negation

import arc.ArcGraphConfig
import arc.ifWsd
import arc.util.copy
import arc.util.decompose
import arc.wsd.disambiguateBy
import de.kimanufaktur.nsm.decomposition.Concept

fun Concept.resolveNegation(
    config: ArcGraphConfig,
    markedContext: String
) = assignedSenseKeys.ifEmpty { definitions.map { it.sensekey } }
    .mapNotNull { senseKey -> senseKeyToAntonymsMap[senseKey] }
    .flatten().firstOrNull()
    ?.decompose(config.depth)
    ?.ifWsd(config) { disambiguateBy(markedContext) }
    ?: pseudoAntonym()


private fun Concept.pseudoAntonym() = copy().also {
    it.negated = true
    it.senseKeyToSynonymsMap = senseKeyToAntonymsMap
    it.senseKeyToAntonymsMap = senseKeyToSynonymsMap
}