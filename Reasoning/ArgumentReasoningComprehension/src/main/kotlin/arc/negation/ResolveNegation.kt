package arc.negation

import arc.ArcGraphConfig
import arc.ifWsd
import arc.util.copy
import arc.util.decompose
import arc.wsd.disambiguateBy
import de.kimanufaktur.nsm.decomposition.Concept
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.semgraph.SemanticGraphEdge

fun Concept.resolveNegation(
    config: ArcGraphConfig,
    coreLabel: CoreLabel,
    markedContext: String,
    negationEdges: List<SemanticGraphEdge>
) =
    if (coreLabel.isNegated(negationEdges))
        assignedSenseKeys.ifEmpty { definitions.map { it.sensekey} }
            .mapNotNull { senseKey -> senseKeyToAntonymsMap[senseKey] }
            .flatten().firstOrNull()
            ?.decompose(config.depth)
            ?.ifWsd(config) { disambiguateBy(markedContext) }
            ?: pseudoAntonym()
    else this


private fun Concept.pseudoAntonym() = copy().also {
    it.negated = true
    it.senseKeyToSynonymsMap = senseKeyToAntonymsMap
    it.senseKeyToAntonymsMap = senseKeyToSynonymsMap
}

fun CoreLabel.isNegated(negationEdges: List<SemanticGraphEdge>) = negationEdges
    .any { edge -> edge.source.backingLabel() == this }