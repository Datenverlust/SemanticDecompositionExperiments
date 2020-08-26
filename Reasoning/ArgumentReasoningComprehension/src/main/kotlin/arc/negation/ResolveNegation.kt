package arc.negation

import arc.ArcGraphConfig
import arc.ifWsd
import arc.util.copy
import arc.util.decompose
import arc.util.syntaxEdges
import arc.wsd.disambiguateBy
import de.kimanufaktur.nsm.decomposition.Concept
import edu.stanford.nlp.pipeline.CoreDocument

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

fun CoreDocument.findNegationTargets() = syntaxEdges().filter { edge -> edge.relation.shortName == "neg" }
    .map { edge -> edge.source.backingLabel() }