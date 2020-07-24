package arc.util

import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import java.util.Properties

private val stanfordPipeline = StanfordCoreNLP(
    Properties()
        .also {
            it.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, ner")
            it.setProperty("ner.useSUTime", "false")
            it.setProperty("depparse.extradependencies", "MAXIMAL")
        }
)

fun String.asAnnotatedCoreDocument() = CoreDocument(this)
    .also { stanfordPipeline.annotate(it) }