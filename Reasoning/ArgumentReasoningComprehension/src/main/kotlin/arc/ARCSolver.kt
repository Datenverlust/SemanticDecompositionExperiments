package arc

import arc.srl.SemanticRoleLabeler
import arc.util.allElements
import arc.util.createNerGraph
import arc.util.createRolesGraph
import arc.util.createSemanticGraph
import arc.util.createSyntaxGraph
import arc.util.getPipelineProperties
import arc.util.mergeGraphes
import arc.wsd.WSDRequest
import arc.wsd.WordSense
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.DoubleMarkerPassing
import de.kimanufaktur.nsm.graph.entities.marker.DoubleMarkerWithOrigin
import de.kimanufaktur.nsm.graph.entities.nodes.DoubleNodeWithMultipleThresholds
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.CoreDocument
import edu.stanford.nlp.pipeline.CoreSentence
import edu.stanford.nlp.pipeline.StanfordCoreNLP

class ARCSolver() {
    private val syntaxPipeline = StanfordCoreNLP(getPipelineProperties())
    private val roleLabeler = SemanticRoleLabeler()

    private val conceptToContextMap = mapOf<Concept, Set<String>>()

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

        val graph = graphElements
            .map { it.asCoreDocument() }
            .map { createGraph(it) }
            .let { mergeGraphes(it) }

        val contextSensitiveVertices = graph.vertexSet().filter { it.assignedContexts.isNotEmpty() }

        val markerPassing = ArcMarkerPassing(
            graph,
            createThresholdMap(contextSensitiveVertices, thresholdElements),
            DoubleNodeWithMultipleThresholds::class.java
        )
            .also { markerPassing ->
                createStartActivationMap(contextSensitiveVertices, startActivationElements)
                    .let { markerPassing.doInitialMarking(it) }
            }
            .also { it.execute() }
        return evaluateMarkerPassing(
            markerPassing = markerPassing,
            warrant0Elements = warrant0Elements,
            warrant1Elements = warrant1Elements
        )
    }

    private fun String.asCoreDocument() = CoreDocument(this)
        .also { syntaxPipeline.annotate(it) }

    private fun getSenseKeysByContext(decomposed: Concept, word: CoreLabel, context: CoreDocument): List<String> {
        WSDRequest(
            markedContext = context.markTarget(word),
            wordSenses = decomposed.getWordSenses()
        )
            .let {
                return decomposed.availableSensekeys.toList().shuffled().take(2)//wsdClient.disambiguate(it)
            }
    }

    private fun CoreDocument.markTarget(coreLabel: CoreLabel): String {
        val tokens = this.tokens()
        val index = tokens.indexOf(coreLabel)
        val words = tokens.map { it.word() }
        return words.asSequence().take(index)
            .plus("\"")
            .plus(coreLabel.word())
            .plus("\"")
            .plus(words.drop(index + 1))
            .joinToString(" ")
    }

    private fun Concept.getWordSenses() = this.sensekeyToDefinitionsMap
        .toList()
        .map { (id, glossDef) ->
            WordSense(
                gloss = glossDef.toString(),
                senseKey = id
            )
        }

    private fun decomposeSentence(sentence: CoreSentence, context: CoreDocument) = sentence.tokens()
        .map { coreLabel ->
            val decomposed = decomposeWord(coreLabel.originalText())
            val senseKeys = getSenseKeysByContext(decomposed, coreLabel, context)
            decomposed.assignedSensekeys = senseKeys.toSet()
            val contextList = conceptToContextMap[decomposed]?.toMutableList() ?: mutableListOf()
            decomposed.assignedContexts = contextList.toSet()
            coreLabel to decomposed
        }
        .toMap()

    private fun createGraph(context: CoreDocument) = context.sentences()
        .map { sentence ->
            decomposeSentence(
                sentence = sentence,
                context = context
            ).let {
                listOf(
                    createSemanticGraph(it.values),
                    createSyntaxGraph(it, sentence),
                    createNerGraph(it),
                    createRolesGraph(it, mapOf())//roleLabeler.invoke(sentence))
                )
            }
        }
        .flatten()
        .let { mergeGraphes(it) }

    private fun createStartActivationMap(
        vertices: List<Concept>,
        startActivationElements: Collection<String>
    ) = vertices
        .filter { vertex -> vertex.assignedContexts.any { context -> context in startActivationElements } }
        .map { vertex ->
            DoubleMarkerWithOrigin().also {
                it.activation = startActivation
                it.origin = vertex
            }
                .let { vertex to listOf(it) }
        }
        .toMap()

    private fun createThresholdMap(
        vertices: List<Concept>,
        thresholdElements: Collection<String>
    ) = vertices
        .filter { vertex -> vertex.assignedContexts.any { context -> context in thresholdElements } }
        .map { vertex -> vertex to threshold }
        .toMap()

    private fun evaluateMarkerPassing(
        markerPassing: DoubleMarkerPassing,
        warrant0Elements: Collection<String>,
        warrant1Elements: Collection<String>
    ) = markerPassing.nodes
        .map { (_, node) -> node as DoubleNodeWithMultipleThresholds }
        .map { node -> node.activationHistory.map { it as DoubleMarkerWithOrigin } }
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

    private fun evaluateActivationMap(
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
        .maxBy { (_, score) -> score }
        ?.let { (label, _) -> label }
        ?: ArcLabel.UNKNOWN
}