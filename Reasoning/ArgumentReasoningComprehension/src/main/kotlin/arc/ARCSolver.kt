package arc

import de.kimanufaktur.nsm.decomposition.Decomposition
import de.kimanufaktur.nsm.decomposition.Definition
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig
import de.kimanufaktur.nsm.graph.entities.marker.DoubleMarkerWithOrigin
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds

class ARCSolver() {
    init {
        Decomposition.init()
    }

    fun invoke(task: ArcTask): Label {

        //elements with start activation
        val activationMap = listOf(task.claim, task.reason)
            .map { elem ->
                Definition(elem).definition.toList()
                    .filterNot { it in Decomposition.getConcepts2Ignore() }
            }
            .flatten()
            .distinct()
            .let { startActivationConcepts ->

                val startActivationMap = startActivationConcepts
                    .map { word ->
                        DoubleMarkerWithOrigin().also {
                            it.activation = MarkerPassingConfig.getStartActivation() * 100
                            it.origin = word
                        }
                            .let { word to listOf(it) }
                    }
                    .toMap()
                    .let { listOf(it) }

                val thresholdMap = startActivationConcepts
                    .map { it to MarkerPassingConfig.getThreshold() }
                    .toMap()

                //elements to build the graph of semantic decomposition
                task.getConceptElements()
                    .map { elem ->
                        Definition(elem).definition
                            .map { word ->
                                getGraph(
                                    word = word,
                                    context = elem
                                )
                            }
                    }
                    .flatten()
                    .reduce { acc, graph -> mergeGraph(acc, graph) }
                    .let { graph ->
                        DoubleMarkerPassing(graph, thresholdMap, DoubleNodeWithMultipleThresholds::class.java)
                            .also { DoubleMarkerPassing.doInitialMarking(startActivationMap, it) }
                    }
            }
            .also { it.execute() }
            .nodes
            .map { (_, node) -> node as DoubleNodeWithMultipleThresholds }
            .map { it.activationHistory.map { it as DoubleMarkerWithOrigin } }
            .flatten()
            .map { it.origin to it.activation }
            .toMap()

        //check activation for these elements after markerpassing is executed
        return mapOf(
            Label.W0 to task.warrant0,
            Label.W1 to task.warrant1
        )
            .mapValues { (_, warrant) -> Definition(warrant).definition.toList() }
            .mapValues { (_, concepts) ->
                concepts.mapNotNull {
                    activationMap.get(it)
                }
                    .average()
            }
            .maxBy { (_, score) -> score }
            ?.key ?: Label.UNKNOWN
    }
}
