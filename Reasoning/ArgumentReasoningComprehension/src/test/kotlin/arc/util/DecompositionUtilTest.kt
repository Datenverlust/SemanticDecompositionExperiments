package arc.util

import arc.ArcConfig
import de.kimanufaktur.nsm.decomposition.Concept
import org.junit.Assert
import org.junit.Test

class DecompositionUtilTest {
    @Test
    fun decompositionDepthTest() {
        (0..3).forEach { depth ->
            val decomposed = "mother".decompose(ArcConfig(depth = 1), "NN")
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
    fun stopWordsTest() {
        listOf("a", "the", "that", "on", "of", "by").forEach { stopWord ->
            Assert.assertTrue(stopWord.isStopWord())
        }
    }

    @Test
    fun copyTest() {
        val conBefore = Concept("mother")
        val conAfter = conBefore.copy().also { it.assignedSenseKeys = setOf("senseKey") }
        Assert.assertNotEquals(conBefore, conAfter)
    }
}