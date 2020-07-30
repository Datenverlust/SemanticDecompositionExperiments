package arc.util

import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.Decomposition

private val semanticDecomposition = Decomposition()

fun Concept.decompose(depth: Int): Concept = semanticDecomposition.decompose(this.litheral, this.wordType, depth)