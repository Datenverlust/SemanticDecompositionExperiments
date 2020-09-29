package arc.util

import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.StanfordCoreNLP
import edu.stanford.nlp.util.logging.RedwoodConfiguration
import java.util.Properties

private val stanfordPipeline = StanfordCoreNLP(
    Properties()
        .also {
            RedwoodConfiguration.current().clear().apply()
            it.setProperty("annotators", "tokenize, ssplit, pos, lemma,  parse, ner")
            it.setProperty("ner.useSUTime", "false")
            it.setProperty("parse.originalDependencies", "true")
        }
)

fun String.asAnnotatedCoreDocument() = CoreDocument(this)
    .also { stanfordPipeline.annotate(it) }