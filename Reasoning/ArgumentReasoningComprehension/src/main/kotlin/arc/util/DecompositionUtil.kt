package arc.util

import arc.ArcGraphConfig
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.Decomposition
import de.kimanufaktur.nsm.decomposition.WordType
import edu.stanford.nlp.ling.CoreLabel

private val semanticDecomposition = Decomposition()

fun Concept.decompose(depth: Int) = semanticDecomposition.decompose(
    lemma?:litheral,
    wordType,
    depth
)

fun CoreLabel.decompose(config: ArcGraphConfig): Concept =
    if (!config.useSemDec || lemma().isStopWord() || (config.useNer && isNamedEntity())) {
        Concept(lemma(), WordType.getType(tag()))
    } else {
        semanticDecomposition.decompose(
            lemma(),
            WordType.getType(tag()),
            config.depth
        )
    }

fun String.decompose(config: ArcGraphConfig, wordType: String): Concept =
    if (!config.useSemDec || isStopWord()) {
        Concept(this, WordType.getType(wordType))
    } else {
        semanticDecomposition.decompose(
            this,
            WordType.getType(wordType),
            config.depth
        )
    }

private val stopWords = {}::class.java.getResourceAsStream("stopwords.txt").bufferedReader().readLines()

fun String.isStopWord() = isBlank() || this in stopWords

fun Concept.isStopWord() = (lemma ?: litheral).isStopWord()

fun Concept.copy() = Concept(litheral).also { con ->
    con.lemma = lemma
    con.wordType = wordType
    con.decompositionlevel = decompositionlevel
    con.senseKeyToSynonymsMap = senseKeyToSynonymsMap
    con.senseKeyToAntonymsMap = senseKeyToAntonymsMap
    con.senseKeyToHyponymsMap = senseKeyToHyponymsMap
    con.senseKeyToHypernymsMap = senseKeyToHypernymsMap
    con.senseKeyToMeronymsMap = senseKeyToMeronymsMap
    con.assignedSenseKeys = assignedSenseKeys
    con.definitions = definitions
}