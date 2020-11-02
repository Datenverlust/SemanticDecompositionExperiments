package arc.srl

import arc.util.asAnnotatedCoreDocument
import org.junit.Assert
import org.junit.Test

class IdentifySemanticRolesTest {
    @Test
    fun singleVerbTest() {
        "He lends me his computer.".asAnnotatedCoreDocument()
            .let { coreDoc ->
                val roles = identifySemanticRoles(coreDoc)
                val tokens = coreDoc.tokens()
                Assert.assertTrue(roles.size == 3)
                Assert.assertEquals(listOf("lender"), roles[tokens[0]])
                Assert.assertEquals(listOf("lent-to"), roles[tokens[2]])
                Assert.assertEquals(listOf("thing lent"), roles[tokens[4]])
            }
    }

    @Test
    fun auxVerbTest() {
        "He tries to buy a computer.".asAnnotatedCoreDocument()
            .let { coreDoc ->
                val roles = identifySemanticRoles(coreDoc)
                val tokens = coreDoc.tokens()
                Assert.assertTrue(roles.size == 3)
                Assert.assertEquals(listOf("Agent/Entity Trying", "buyer"), roles[tokens[0]])
                Assert.assertEquals(listOf("thing tried"), roles[tokens[2]])
                Assert.assertEquals(listOf("thing bought"), roles[tokens[5]])
            }
    }

    @Test
    fun duplicatedVerbsTest() {
        "He buys a bike because she bought a computer.".asAnnotatedCoreDocument()
            .let { coreDoc ->
                val roles = identifySemanticRoles(coreDoc)
                val tokens = coreDoc.tokens()
                Assert.assertTrue(roles.size == 4)
                Assert.assertEquals(listOf("buyer"), roles[tokens[0]])
                Assert.assertEquals(listOf("thing bought"), roles[tokens[3]])
                Assert.assertEquals(listOf("buyer"), roles[tokens[5]])
                Assert.assertEquals(listOf("thing bought"), roles[tokens[8]])
            }
    }

    @Test
    fun multipleSentencesTest() {
        "He tries to buy a computer. He lends me his computer.".asAnnotatedCoreDocument()
            .let { coreDoc ->
                val roles = identifySemanticRoles(coreDoc)
                val tokens = coreDoc.tokens()
                Assert.assertTrue(roles.size == 6)
                Assert.assertEquals(listOf("Agent/Entity Trying", "buyer"), roles[tokens[0]])
                Assert.assertEquals(listOf("thing tried"), roles[tokens[2]])
                Assert.assertEquals(listOf("thing bought"), roles[tokens[5]])
                Assert.assertEquals(listOf("lender"), roles[tokens[7]])
                Assert.assertEquals(listOf("lent-to"), roles[tokens[9]])
                Assert.assertEquals(listOf("thing lent"), roles[tokens[11]])
            }
    }
}