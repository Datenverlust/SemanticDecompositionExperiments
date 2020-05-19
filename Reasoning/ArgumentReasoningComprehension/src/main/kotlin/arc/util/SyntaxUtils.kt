package arc.util

import java.util.Properties

fun getPipelineProperties() = Properties()
    .also {
        it.setProperty("annotators", "tokenize, ssplit, pos, lemma, parse, ner")
        it.setProperty("ner.useSUTime", "false")
        it.setProperty("depparse.extradependencies", "MAXIMAL")
    }