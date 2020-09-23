package arc

import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.CoreDocument
import org.jgrapht.graph.DefaultListenableGraph

data class ArcTask(
    val id: String,
    val warrant0: String,
    val warrant1: String,
    val correctLabelW0orW1: ArcLabel,
    val reason: String,
    val claim: String,
    val debateTitle: String,
    val debateInfo: String
)

enum class ArcLabel {
    W0,
    W1,
    UNKNOWN
}

data class GraphData(
    val context: String,
    val coreDoc: CoreDocument,
    val conceptMap: Map<CoreLabel, Concept>,
    val graph: DefaultListenableGraph<Concept, WeightedEdge>
)

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
    val score: List<Double>,
    val numVertices: Int,
    val numEdges: Int
)

fun Concept.ifWsd(config: ArcConfig, transformer: Concept.() -> Concept) =
    if (config.useWsd) this.transformer() else this