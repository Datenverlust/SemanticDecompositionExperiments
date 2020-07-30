package arc.util

import edu.stanford.nlp.pipeline.CoreDocument

fun CoreDocument.syntaxEdges() = sentences().map { it.dependencyParse().edgeListSorted() }.flatten()