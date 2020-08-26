package arc.negation

import arc.ArcGraphConfig
import arc.ifWsd
import arc.util.copy
import arc.util.decompose
import arc.util.syntaxEdges
import arc.wsd.disambiguateBy
import de.kimanufaktur.nsm.decomposition.Concept
import edu.stanford.nlp.pipeline.CoreDocument

private val verbTags = listOf("MD", "VB", "VBD", "VBG", "VBN", "VBP", "VBZ")
private val nounTags = listOf("NN", "NNP", "NNS", "NNPS")
private val adjectiveTags = listOf("JJ", "JJR", "JJS")
private val adverbTags = listOf("RB", "RBR", "RBS")
private val negatedTags = listOf(verbTags, nounTags, adjectiveTags, adverbTags).flatten()

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

fun CoreDocument.findNegationTargets() = syntaxEdges().let { edgeList ->
    edgeList.filter { edge -> edge.relation.shortName == "neg" }
        .map { edge -> edge.source.backingLabel() }
        .filter { sourceLabel -> sourceLabel.tag() in negatedTags }
        .mapNotNull { sourceLabel ->
            if (sourceLabel.tag() in nounTags) {
                edgeList
                    .filter { edge ->
                        edge.source.backingLabel() == sourceLabel
                            && edge.target.backingLabel().tag() in negatedTags
                    }
                    .filterNot { edge -> edge.target.backingLabel().tag() in nounTags }
                    .let { relevantEdges ->
                        relevantEdges.firstOrNull { edge -> edge.relation.shortName == "amod" }
                            ?: relevantEdges.firstOrNull { edge -> edge.relation.shortName == "vmod" }
                            ?: relevantEdges.firstOrNull { edge -> edge.relation.shortName == "rcmod" }
                            ?: relevantEdges.firstOrNull { edge -> edge.relation.shortName == "advmod" }
                            ?: relevantEdges.firstOrNull { edge -> edge.relation.shortName == "cop" }
                        //TODO: check negations that could be solved replacement ...
                        // "no one" - "nobody"
                        // "no way" - "noway"/"never"
                    }
                    ?.target
                    ?.backingLabel()
                    .let {
                        it
                    }
            } else sourceLabel
        }
}