package arc

import de.kimanufaktur.nsm.decomposition.Decomposition
import de.kimanufaktur.nsm.decomposition.Definition
import de.kimanufaktur.nsm.decomposition.graph.conceptCache.GraphUtil
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig

class ARCSolver() {
    internal val decomposition = Decomposition()

    fun invoke(task: ArcTask): Label {
        val graphs = listOf(
            task.warrant0,
            task.warrant1,
            task.reason,
            task.claim,
            task.debateTitle,
            task.debateInfo
        )
            .map { str ->
                val def = Definition(str)
                def.definition.map { word ->
                    GraphUtil.getGraph(word.litheral, word.wordType, MarkerPassingConfig.getDecompositionDepth())
                }
            }
        return Label.UNKNOWN
    }
}