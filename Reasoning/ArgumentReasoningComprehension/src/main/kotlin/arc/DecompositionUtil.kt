package arc

import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.Decomposition

private val semanticDecomposition = Decomposition()

fun String.decompose(depth: Int = defaultDecompositionDepth): Concept = semanticDecomposition.decompose(Concept(this), depth)

fun Concept.decompose(depth: Int = defaultDecompositionDepth): Concept = semanticDecomposition.decompose(this, depth)