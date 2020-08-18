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

data class GraphComponent(
    val context: String,
    val coreDoc: CoreDocument,
    val conceptMap: Map<CoreLabel, Concept>,
    val graph: DefaultListenableGraph<Concept, WeightedEdge>
)

data class ArcGraphConfig(
    val depth: Int = 2,
    val useSemDec: Boolean = true,
    val useSyntax: Boolean = true,
    val useSrl: Boolean = true,
    val useNer: Boolean = true,
    val useWsd: Boolean = false,
    val useNeg: Boolean = true
)

fun Concept.ifWsd(config: ArcGraphConfig, transformer: Concept.() -> Concept) =
    if (config.useWsd) this.transformer() else this