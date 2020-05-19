package arc.util

import arc.antonymLinkWeight
import arc.decomposeWord
import arc.definitionLinkWeight
import arc.hypernymLinkWeight
import arc.hyponymLinkWeight
import arc.meronymLinkWeight
import arc.namedEntityLinkWeight
import arc.semanticRoleLinkWeight
import arc.synonymLinkWeight
import arc.syntaxLinkWeight
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.Decomposition
import de.kimanufaktur.nsm.decomposition.graph.edges.AntonymEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.DefinitionEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.EdgeType
import de.kimanufaktur.nsm.decomposition.graph.edges.HypernymEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.HyponymEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.MeronymEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.NamedEntityEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.SemanticRoleEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.SynonymEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.SyntaxEdge
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import edu.stanford.nlp.ling.CoreLabel
import edu.stanford.nlp.pipeline.CoreSentence
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultListenableGraph

fun createSemanticGraph(conceptList: Collection<Concept>) = conceptList.map { concept ->
    DefaultListenableGraph(
        DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
    )
        .also { graph ->
            if (concept !in Decomposition.getConcepts2Ignore()) {
                addConceptRecursivly(graph, concept)
            }
        }
}.let { mergeGraphes(it) }

fun createSyntaxGraph(decomposedInput: Map<CoreLabel, Concept>, sentence: CoreSentence) = DefaultListenableGraph(
    DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
).also { graph ->
    sentence.tokens()
        .mapNotNull { decomposedInput[it] }
        .forEach { graph.addVertex(it) }

    sentence.dependencyParse().edgeListSorted()
        .forEach { syntaxEge ->
            val source = syntaxEge.source.backingLabel().let { decomposedInput.getValue(it) }
            val target = syntaxEge.target.backingLabel().let { decomposedInput.getValue(it) }
            createEdge(
                edgeType = EdgeType.Syntax,
                source = source,
                target = target,
                syntaxRelation = syntaxEge.relation.longName
            )?.let { edge ->
                if (edge !in graph.getAllEdges(source, target)) {
                    graph.addEdge(source, target, edge)
                    graph.setEdgeWeight(edge, edge.edgeWeight)
                }
            }
        }
}

fun createNerGraph(decomposedInput: Map<CoreLabel, Concept>) = DefaultListenableGraph(
    DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
).also { graph ->
    decomposedInput.filterKeys { it.ner() != "O" }
        .forEach { (coreLabel, name) ->
            graph.addVertex(name)
            val entity = decomposeWord(coreLabel.ner().toLowerCase())
                .also { addConceptRecursivly(graph, it) }
            createEdge(
                edgeType = EdgeType.NamedEntity,
                source = name,
                target = entity
            )?.let { edge ->
                if (edge !in graph.getAllEdges(name, entity)) {
                    graph.addEdge(name, entity, edge)
                    graph.setEdgeWeight(edge, edge.edgeWeight)
                }
            }
        }
}

fun createRolesGraph(decomposedInput: Map<CoreLabel, Concept>, rolesMap: Map<CoreLabel, List<String>>) = DefaultListenableGraph(
    DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
).also { graph ->
    rolesMap.forEach { (coreLabel, roles) ->
        val source = decomposedInput.getValue(coreLabel)
            .also { graph.addVertex(it) }
        roles
            .map { role ->
                decomposeWord(role.toLowerCase())
                    .also { addConceptRecursivly(graph, it) }
            }
            .forEach { roleConcept ->
                graph.addVertex(roleConcept)
                createEdge(
                    edgeType = EdgeType.SemanticRole,
                    source = source,
                    target = roleConcept
                )?.let { edge ->
                    if (edge !in graph.getAllEdges(source, roleConcept)) {
                        graph.addEdge(source, roleConcept, edge)
                        graph.setEdgeWeight(edge, edge.edgeWeight)
                    }
                }
            }
    }
}

fun createEdge(edgeType: EdgeType, source: Concept, target: Concept, syntaxRelation: String? = null): WeightedEdge? {
    val edgetypeAttribute = "edgeType"
    return when (edgeType) {
        EdgeType.Synonym -> createEdge(
            edge = SynonymEdge(),
            source = source,
            target = target,
            weight = synonymLinkWeight,
            attributes = mapOf(edgetypeAttribute to "synonym")
        )
        EdgeType.Definition -> createEdge(
            edge = DefinitionEdge(),
            source = source,
            target = target,
            weight = definitionLinkWeight,
            attributes = mapOf(edgetypeAttribute to "definition")
        )
        EdgeType.Hypernym -> createEdge(
            edge = HypernymEdge(),
            source = source,
            target = target,
            weight = hypernymLinkWeight,
            attributes = mapOf(edgetypeAttribute to "hypernym")
        )
        EdgeType.Hyponym -> createEdge(
            edge = HyponymEdge(),
            source = source,
            target = target,
            weight = hyponymLinkWeight,
            attributes = mapOf(edgetypeAttribute to "hyponym")
        )
        EdgeType.Antonym -> createEdge(
            edge = AntonymEdge(),
            source = source,
            target = target,
            weight = antonymLinkWeight,
            attributes = mapOf(edgetypeAttribute to "antonym")
        )
        EdgeType.Meronym -> createEdge(
            edge = MeronymEdge(),
            source = source,
            target = target,
            weight = meronymLinkWeight,
            attributes = mapOf(edgetypeAttribute to "meronym")
        )
        EdgeType.Syntax -> createEdge(
            edge = SyntaxEdge(),
            source = source,
            target = target,
            weight = syntaxLinkWeight,
            attributes = mapOf(
                edgetypeAttribute to "syntax",
                "syntaxRelation" to (syntaxRelation ?: "")
            )
        )
        EdgeType.NamedEntity -> createEdge(
            edge = NamedEntityEdge(),
            source = source,
            target = target,
            weight = namedEntityLinkWeight,
            attributes = mapOf(
                edgetypeAttribute to "namedEntity"
            )
        )
        EdgeType.SemanticRole -> createEdge(
            edge = SemanticRoleEdge(),
            source = source,
            target = target,
            weight = semanticRoleLinkWeight,
            attributes = mapOf(
                edgetypeAttribute to "semanticRole"
            )
        )
        else -> null
    }
}

fun createEdge(edge: WeightedEdge, source: Concept, target: Concept, weight: Double, attributes: Map<String, String>): WeightedEdge {
    edge.source = source
    edge.target = target
    edge.weight = weight
    edge.attributes = attributes
    return edge
}

fun addConceptRecursivly(graph: DefaultListenableGraph<Concept, WeightedEdge>, source: Concept) {
    if (!Decomposition.getConcepts2Ignore().contains(source)) {
        if (!graph.containsVertex(source)) {
            val senseKeys =
                if (source.assignedSensekeys.isNotEmpty()) {
                    source.assignedSensekeys
                } else {
                    source.availableSensekeys
                }

            graph.addVertex(source)

            mapOf(
                EdgeType.Synonym to source.sensekeyToSynonymsMap,
                EdgeType.Definition to source.sensekeyToDefinitionsMap.mapValues { (_, def) -> def.definition },
                EdgeType.Hyponym to source.sensekeyToHyponymsMap,
                EdgeType.Hypernym to source.sensekeyToHypernymsMap,
                EdgeType.Antonym to source.sensekeyToAntonymsMap,
                EdgeType.Meronym to source.sensekeyToMeronymsMap
            )
                .mapValues { (_, senseKeyMap) ->
                    senseKeyMap.filterKeys { it in senseKeys }.values.flatten()
                }
                .map { (edgeType, concepts) ->
                    concepts.mapNotNull { it?.let { edgeType to it } }
                }
                .flatten()
                .filterNot { (_, target) -> target in Decomposition.getConcepts2Ignore() || source == target }
                .forEach { (edgeType, target) ->
                    addConceptRecursivly(graph, target)
                    createEdge(
                        edgeType = edgeType,
                        source = source,
                        target = target
                    )
                        ?.let { edge ->
                            if (edge !in graph.getAllEdges(source, target)) {
                                graph.addEdge(source, target, edge)
                                graph.setEdgeWeight(edge, edge.edgeWeight)
                            }
                        }
                }
        }
    }
}

fun mergeGraphes(graphList: List<Graph<Concept, WeightedEdge>>) =
    DefaultListenableGraph(
        DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
    ).also { graph ->
        graphList
            .map { it.vertexSet() }
            .flatten()
            .sortedByDescending { it.assignedContexts.size }
            .distinct()
            .forEach { graph.addVertex(it) }
        graphList
            .map { it.edgeSet() }
            .flatten()
            .distinct()
            .forEach {
                graph.addEdge(it.source as Concept, it.target as Concept, it)
                graph.setEdgeWeight(it, it.edgeWeight)
            }
    }