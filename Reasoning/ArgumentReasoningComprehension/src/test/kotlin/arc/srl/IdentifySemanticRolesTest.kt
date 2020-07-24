package arc.srl

import arc.util.asAnnotatedCoreDocument
import org.junit.Assert
import org.junit.Test

class IdentifySemanticRolesTest {
    @Test
    fun singleVerbTest() {
        "He lends me his computer.".asAnnotatedCoreDocument().sentences().first()
            .let { coreSentence ->
                val roles = identifySemanticRoles(coreSentence)
                Assert.assertTrue(roles.size == 3)
                Assert.assertEquals(roles[coreSentence.tokens()[0]], listOf("lender"))
                Assert.assertEquals(roles[coreSentence.tokens()[2]], listOf("lent-to"))
                Assert.assertEquals(roles[coreSentence.tokens()[4]], listOf("thing lent"))
            }
    }

    @Test
    fun auxVerbTest() {
        "He tries to buy a computer.".asAnnotatedCoreDocument().sentences().first()
            .let { coreSentence ->
                val roles = identifySemanticRoles(coreSentence)
                Assert.assertTrue(roles.size == 3)
                Assert.assertEquals(roles[coreSentence.tokens()[0]], listOf("Agent/Entity Trying", "buyer"))
                Assert.assertEquals(roles[coreSentence.tokens()[2]], listOf("thing tried"))
                Assert.assertEquals(roles[coreSentence.tokens()[5]], listOf("thing bought"))
            }
    }

    @Test
    fun duplicatedVerbsTest() {
        "He buys a bike because she bought a computer.".asAnnotatedCoreDocument().sentences().first()
            .let { coreSentence ->
                val roles = identifySemanticRoles(coreSentence)
                Assert.assertTrue(roles.size == 4)
                Assert.assertEquals(roles[coreSentence.tokens()[0]], listOf("buyer"))
                Assert.assertEquals(roles[coreSentence.tokens()[3]], listOf("thing bought"))
                Assert.assertEquals(roles[coreSentence.tokens()[5]], listOf("buyer"))
                Assert.assertEquals(roles[coreSentence.tokens()[8]], listOf("thing bought"))
            }
    }
}