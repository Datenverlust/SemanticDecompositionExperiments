package arc

import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.CoreDocument
import org.jgrapht.Graph

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
    val graph: Graph<Concept, WeightedEdge>
)

data class ArcGraphConfig(
    val decompositionDepth: Int = 2,
    val useSemanticGraph: Boolean = true,
    val useSyntaxDependencies: Boolean = false,
    val useSemanticRoles: Boolean = false,
    val useNamedEntities: Boolean = false,
    val useWsd: Boolean = false,
    val useNegationHandling: Boolean = false
)