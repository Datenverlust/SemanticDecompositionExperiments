package arc.srl

import org.junit.Assert
import org.junit.Test

class ReadFrameSetTest {
    @Test
    fun testFramesDir() {
        Assert.assertTrue(framesDir.isDirectory)
        framesDir.listFiles()
            ?.filterNot { it.name == "frameset.dtd" }
            ?.forEach {
                val predicate = it.name.substringBefore(".xml")
                Assert.assertNotNull("frameSet for lemma $predicate must exist", getFrameset(predicate))
            }
    }

    @Test
    fun testUnknownLemma() {
        Assert.assertNull("unknown lemma must not exist", getRoleSet("unknown", "0"))
    }

    @Test
    fun testUnknownSense() {
        val predicate = "play"
        Assert.assertNotNull("known sense play.0 must exist", getRoleSet(predicate, "$predicate.01"))
        Assert.assertNull("unknown sense play.7 must not exist", getRoleSet(predicate, "7"))
    }
}