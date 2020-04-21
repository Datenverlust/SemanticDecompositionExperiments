package arc

import arc.util.allElements
import arc.util.getGraph
import arc.util.mergeGraph
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.Definition
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig
import de.kimanufaktur.nsm.graph.entities.marker.DoubleMarkerWithOrigin
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds

class ARCSolver() {
    fun invoke(task: ArcTask): ArcLabel {
        val allElements = task.allElements()
        return invoke(
            graphElements = allElements,
            startActivationElements = listOf(task.claim, task.reason),
            thresholdElements = allElements,
            warrant0Elements = listOf(task.warrant0),
            warrant1Elements = listOf(task.warrant1)
        )
    }

    fun invoke(
        graphElements: Collection<String>,
        startActivationElements: Collection<String>,
        thresholdElements: Collection<String>,
        warrant0Elements: Collection<String>,
        warrant1Elements: Collection<String>
    ): ArcLabel {
        val semanticGraph = createSemanticGraph(graphElements)
        val vertexSet = semanticGraph.vertexSet()


        val markerPassing = ArcMarkerPassing(
            semanticGraph,
            createThresholdMap(vertexSet, thresholdElements),
            DoubleNodeWithMultipleThresholds::class.java
        )
            .also { markerpassing ->
                createStartActivationMap(vertexSet, startActivationElements)
                    .let { markerpassing.doInitialMarking(it) }
            }
            .also { it.execute() }
        return evaluateMarkerpassing(
            markerPassing = markerPassing,
            warrant0Elements = warrant0Elements,
            warrant1Elements = warrant1Elements
        )
    }

    internal fun createSemanticGraph(graphElements: Collection<String>) = graphElements
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
        .let { graphList -> mergeGraph(graphList) }

    internal fun createStartActivationMap(
        vertexSet: Set<Concept>,
        startActivationElements: Collection<String>
    ) = vertexSet
        .filter { vertex -> vertex.assignedContexts.any { context -> context in startActivationElements } }
        .map { vertex ->
            DoubleMarkerWithOrigin().also {
                it.activation = MarkerPassingConfig.getStartActivation() * 100  //put multiplicator in config
                it.origin = vertex
            }
                .let { vertex to listOf(it) }
        }
        .toMap()

    internal fun createThresholdMap(
        verticesSet: Set<Concept>,
        thresholdElements: Collection<String>
    ) = verticesSet
        .filter { vertex -> vertex.assignedContexts.any { context -> context in thresholdElements } }
        .map { vertex -> vertex to MarkerPassingConfig.getThreshold() }
        .toMap()

    internal fun evaluateMarkerpassing(
        markerPassing: DoubleMarkerPassing,
        warrant0Elements: Collection<String>,
        warrant1Elements: Collection<String>
    ) = markerPassing.nodes
        .map { (_, node) -> node as DoubleNodeWithMultipleThresholds }
        .map { it.activationHistory.map { it as DoubleMarkerWithOrigin } }
        .flatten()
        .map { it.origin to it.activation }
        .toMap()
        .let {
            evaluateActivationMap(
                activationMap = it,
                warrant0Elements = warrant0Elements,
                warrant1Elements = warrant1Elements
            )
        }

    internal fun evaluateActivationMap(
        activationMap: Map<Concept, Double>,
        warrant0Elements: Collection<String>,
        warrant1Elements: Collection<String>
    ) = mapOf(
        ArcLabel.W0 to warrant0Elements,
        ArcLabel.W1 to warrant1Elements
    )
        .mapValues { (_, warrantElements) ->
            warrantElements.map { elem ->
                activationMap.filterKeys { concept -> elem in concept.assignedContexts }.values
            }
                .flatten()
                .average()
        }
        .toList()
        .sortedByDescending { (_, score) -> score }
        .firstOrNull()
        ?.let { (label, _) -> label }
        ?: ArcLabel.UNKNOWN
}