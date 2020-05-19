package arc

import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.Decomposition
import java.lang.Exception

internal val semanticDecomposition = Decomposition()

fun decomposeWord(word: String) = Concept(word)
    .let {
        try {
            semanticDecomposition.decompose(it, decompositionDepth)
        } catch (e: Exception) {
            println("Strange Exception for $word with wordtype ${it.wordType}")
            throw e
        }
        it
    }