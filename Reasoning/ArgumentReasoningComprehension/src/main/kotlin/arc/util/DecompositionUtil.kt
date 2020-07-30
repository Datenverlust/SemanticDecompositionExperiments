package arc.util

import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.Decomposition

private val semanticDecomposition = Decomposition()

fun Concept.decompose(depth: Int): Concept = semanticDecomposition.decompose(this.litheral, this.wordType, depth)

private val stopWords = {}::class.java.getResourceAsStream("stopwords.txt").bufferedReader().readLines()

fun Concept.isStopWord() = litheral in stopWords

fun Concept.copy() = Concept(litheral).also {
//    it.decompositionlevel = decompositionlevel
    it.senseKeyToSynonymsMap = senseKeyToSynonymsMap
    it.senseKeyToAntonymsMap = senseKeyToAntonymsMap
    it.senseKeyToHyponymsMap = senseKeyToHyponymsMap
    it.senseKeyToHypernymsMap = senseKeyToHypernymsMap
    it.senseKeyToMeronymsMap = senseKeyToMeronymsMap
    it.senseKeyToGlossMap = senseKeyToGlossMap
}