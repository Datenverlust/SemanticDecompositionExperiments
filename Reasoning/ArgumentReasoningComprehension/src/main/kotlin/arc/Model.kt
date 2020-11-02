package arc

import arc.util.readGraphFromString
import arc.util.writeGraphAsString
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import kotlinx.serialization.Serializable
import org.jgrapht.graph.DefaultListenableGraph

data class ArcTask(
    val id: String,
    val warrant0: String,
    val warrant1: String,
    val correctLabelW0orW1: ArcLabel,
    val reason: String,
    val claim: String,
    val debateTitle: String,
    val debateInfo: String,
    val isAdversarial: Boolean
)

enum class ArcLabel {
    W0,
    W1,
    UNKNOWN
}

data class GraphData(
    val context: String,
    val sourceConcepts: Set<String>,
    val graph: DefaultListenableGraph<String, WeightedEdge>
)

@Serializable
data class CompressedData(
    val context: String,
    val sourceConcepts: Set<String>,
    val graphML: String
)

fun GraphData.compress() = CompressedData(
    context = context,
    sourceConcepts = sourceConcepts,
    graphML = writeGraphAsString(graph)
)

fun CompressedData.decompress() = GraphData(
    context = context,
    sourceConcepts = sourceConcepts,
    graph = readGraphFromString(graphML)
)

fun Concept.asNodeIdentifier() = litheral.replace("""[-/',+\d]""".toRegex(), "") +
    assignedSenseKeys.joinToString("_") { it.replace("""[:%]""".toRegex(), "") }.let { "_$it" } +
    if (negated) "_neg" else ""

data class ArcConfig(
    val depth: Int = 1,
    val useSemDec: Boolean = true,
    val useSyntax: Boolean = true,
    val useSrl: Boolean = true,
    val useNer: Boolean = true,
    val useWsd: Boolean = true,
    val useNeg: Boolean = true,
    val pulseCount: Int = 100,
    val startActivation: Double = 100.0,
    val threshold: Double = 0.1,
    val synonymLinkWeight: Double = 1.0,
    val definitionLinkWeight: Double = 0.5,
    val definitionOfLinkWeight: Double = 0.5,
    val antonymLinkWeight: Double = -0.8,
    val hyponymLinkWeight: Double = 0.2,
    val hypernymLinkWeight: Double = 0.3,
    val meronymLinkWeight: Double = 0.2,
    val holonymLinkWeight: Double = 0.2,
    val syntaxLinkWeight: Double = 0.5,
    val namedEntityLinkWeight: Double = 0.3,
    val nameOfLinkWeight: Double = 0.3,
    val semanticRoleLinkWeight: Double = 0.8,
    val semanticRoleOfLinkWeight: Double = 0.8
)

fun ArcConfig.toDirName() =
    "depth$depth"
        .let { if (useWsd) "${it}-Wsd" else "${it}-noWsd" }
        .let { if (useNeg) "${it}-Neg" else "${it}-noNeg" }
        .let { "$it-${this.hashCode()}" }

data class ArcResult(
    val id: String,
    val foundLabel: ArcLabel,
    val correctLabel: ArcLabel,
    val resultW0: ArcPartialResult,
    val resultW1: ArcPartialResult
)

data class ArcPartialResult(
    val score: Double,
    val graphMeta: GraphMeta
)

data class GraphMeta(
    val numNodes: Int,
    val connected: Boolean,
    val numDefinitionEdges: Int,
    val numDefinitionOfEdges: Int,
    val numSynonymEdges: Int,
    val numAntonymEdges: Int,
    val numHypernymEdges: Int,
    val numHyponymEdges: Int,
    val numMeronymEdges: Int,
    val numHolonymEdges: Int,
    val numSyntaxEdges: Int,
    val numNamedEntityEdges: Int,
    val numNameOfEdges: Int,
    val numSemRoleEdges: Int,
    val numSemRoleOfEdges: Int
) {
    val numTotalEdges: Int = numDefinitionEdges + numDefinitionOfEdges + numSynonymEdges + numAntonymEdges +
        numHypernymEdges + numHyponymEdges + numMeronymEdges + numHolonymEdges + numSyntaxEdges + numNamedEntityEdges +
        numNameOfEdges + numSemRoleEdges + numSemRoleOfEdges
    val propDefinitionEdges: Double = numDefinitionEdges.toDouble() * 2 / numTotalEdges.toDouble()
    val propSynonymEdges: Double = numSynonymEdges.toDouble() / numTotalEdges.toDouble()
    val propAntonymEdges: Double = numAntonymEdges.toDouble() / numTotalEdges.toDouble()
    val propHyperHyponymEdges: Double = numHypernymEdges.toDouble() * 2 / numTotalEdges.toDouble()
    val propMeroHolonymEdges: Double = numMeronymEdges.toDouble() * 2 / numTotalEdges.toDouble()
    val propSyntaxEdges: Double = numSyntaxEdges.toDouble() / numTotalEdges.toDouble()
    val propNamedEntityEdges: Double = numNamedEntityEdges.toDouble() * 2 / numTotalEdges.toDouble()
    val propSemanticRoleEdges: Double = numSemRoleEdges.toDouble() * 2 / numTotalEdges.toDouble()
}

fun Concept.ifWsd(config: ArcConfig, transformer: Concept.() -> Concept) =
    if (config.useWsd) this.transformer() else this