package arc.util

import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.WordType
import org.junit.Assert
import org.junit.Test

class DecompositionUtilTest {
    @Test
    fun decompositionDepthTest() {
        (0..3).forEach { depth ->
            val decomposed = Concept("mother", WordType.NN).decompose(depth)
            if (depth == 0) Assert.assertEquals(-1, decomposed.decompositionlevel)
            else Assert.assertEquals(depth, decomposed.decompositionlevel)
            val allHypernyms = mutableListOf<Concept>()
            var lastCon: Concept? = decomposed
            (0..depth).forEach { generation ->
                val nextConcepts = listOfNotNull(lastCon?.hypernyms, lastCon?.antonyms).flatten()
                lastCon = nextConcepts.filterNot { it in allHypernyms }.firstOrNull()
                if (generation == depth) {
                    Assert.assertNull(lastCon)
                } else {
                    Assert.assertNotNull(lastCon)
                    allHypernyms.addAll(nextConcepts)
                }
            }
        }

    }

    @Test
    fun wordTypeTest() {
        val wordTypeBefore = WordType.NN
        val wordTypeAfter = Concept("mother", wordTypeBefore).decompose(1).wordType
        Assert.assertEquals(wordTypeBefore, wordTypeAfter)
    }

    @Test
    fun stopWordsTest() {
        listOf("a", "the", "that", "on", "of", "by").forEach { stopWord ->
            Assert.assertTrue(Concept(stopWord).isStopWord())
        }
    }

    @Test
    fun copyTest() {
        val conBefore = Concept("mother")
        val conAfter = conBefore.copy().also { it.assignedSenseKeys = setOf("senseKey") }
        Assert.assertNotEquals(conBefore, conAfter)
    }
}