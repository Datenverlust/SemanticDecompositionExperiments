package arc.util

import edu.stanford.nlp.ling.CoreLabel

fun CoreLabel.isNamedEntity() = ner() != null && ner() != "O"