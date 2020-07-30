package arc.util

import arc.ArcGraphConfig
import arc.antonymLinkWeight
import arc.definitionLinkWeight
import arc.hypernymLinkWeight
import arc.hyponymLinkWeight
import arc.meronymLinkWeight
import arc.namedEntityLinkWeight
import arc.semanticRoleLinkWeight
import arc.srl.identifySemanticRoles
import arc.synonymLinkWeight
import arc.syntaxLinkWeight
import arc.toGraphComponent
import de.kimanufaktur.nsm.decomposition.Concept
import de.kimanufaktur.nsm.decomposition.Decomposition
import de.kimanufaktur.nsm.decomposition.WordType
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
import edu.stanford.nlp.pipeline.CoreDocument
import org.jgrapht.Graph
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultListenableGraph
import java.util.Collections

val semanticGraphCache = Collections.synchronizedMap<String, DefaultListenableGraph<Concept, WeightedEdge>>(mutableMapOf())
fun Concept.asKey(config: ArcGraphConfig) = "${hashCode()}#${config.hashCode()}"

fun createSemanticGraph(conceptList: Collection<Concept>, config: ArcGraphConfig) = conceptList.map { source ->
    val key = source.asKey(config)
    semanticGraphCache[key] ?: DefaultListenableGraph(
        DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
    )
        .also { graph ->
            if (source !in Decomposition.getConcepts2Ignore()) {
                graph.addSemanticRelationsRecursive(source, config)
            }
        }
        .also { semanticGraphCache[key] = it }
}
    .merge()

val syntaxGraphCache = Collections.synchronizedMap<String, DefaultListenableGraph<Concept, WeightedEdge>>(mutableMapOf())

fun createSyntaxGraph(conceptMap: Map<CoreLabel, Concept>, coreDoc: CoreDocument) = DefaultListenableGraph(
    DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
).also { graph ->
    conceptMap.values.forEach { concept -> graph.addVertex(concept) }
    coreDoc.syntaxEdges()
        .forEach { syntaxEdge ->
            syntaxEdge.source.backingLabel().let { source -> conceptMap[source] }
                ?.let { source ->
                    syntaxEdge.target.backingLabel().let { target -> conceptMap[target] }
                        ?.let { target ->
                            createEdge(
                                edgeType = EdgeType.Syntax,
                                source = source,
                                target = target,
                                syntaxRelation = syntaxEdge.relation.longName
                            )?.let { edge ->
                                if (edge !in graph.getAllEdges(source, target)) {
                                    graph.addEdge(source, target, edge)
                                    graph.setEdgeWeight(edge, edge.edgeWeight)
                                }
                            }
                        }
                }

        }
}

fun createNerGraph(conceptMap: Map<CoreLabel, Concept>, config: ArcGraphConfig) = DefaultListenableGraph(
    DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
).also { graph ->
    conceptMap
        .filterKeys { it.ner() != null && it.ner() != "O" }
        .forEach { (coreLabel, nameConcept) ->
            graph.addVertex(nameConcept)

            Concept(coreLabel.ner().toLowerCase(), WordType.getType(coreLabel.tag()))
                .let { entityConcept ->
                    if (config.useSemanticGraph) entityConcept.decompose(config.decompositionDepth)
                    else entityConcept
                }
                .also { entityConcept ->
                    graph.addSemanticRelationsRecursive(entityConcept, config)
                }
                .let { entityConcept ->
                    createEdge(
                        edgeType = EdgeType.NamedEntity,
                        source = nameConcept,
                        target = entityConcept
                    )?.let { edge ->
                        if (edge !in graph.getAllEdges(nameConcept, entityConcept)) {
                            graph.addEdge(nameConcept, entityConcept, edge)
                            graph.setEdgeWeight(edge, edge.edgeWeight)
                        }
                    }
                }

        }
}

fun createRolesGraph(
    conceptMap: Map<CoreLabel, Concept>,
    coreDoc: CoreDocument,
    config: ArcGraphConfig
) = DefaultListenableGraph(
    DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
)
    .also { graph ->
        identifySemanticRoles(coreDoc)
            .forEach { (coreLabel, roleList) ->
                val source = conceptMap.getValue(coreLabel)
                    .also { graph.addVertex(it) }
                roleList
                    .map { role ->
                        role.toGraphComponent(config.copy(useSemanticRoles = false)).graph
                    }
                    .forEach { roleGraph ->
                        roleGraph.vertexSet().forEach { graph.addVertex(it) }
                        roleGraph.edgeSet().forEach { edge -> graph.addEdge(edge.source as Concept, edge.target as Concept, edge) }
                        roleGraph.vertexSet()
                            .forEach { roleConcept ->
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
        EdgeType.Meronym -> createEdge(
            edge = MeronymEdge(),
            source = source,
            target = target,
            weight = meronymLinkWeight,
            attributes = mapOf(edgetypeAttribute to "meronym")
        )
        EdgeType.Antonym -> createEdge(
            edge = AntonymEdge(),
            source = source,
            target = target,
            weight = antonymLinkWeight,
            attributes = mapOf(edgetypeAttribute to "antonym")
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

fun DefaultListenableGraph<Concept, WeightedEdge>.addSemanticRelationsRecursive(source: Concept, config: ArcGraphConfig) {
    if (source !in Decomposition.getConcepts2Ignore() && !containsVertex(source)) {
        addVertex(source)
        addDefinitionsBy(source, config)
        addConceptsBy(source, config)
    }
}

fun DefaultListenableGraph<Concept, WeightedEdge>.addDefinitionsBy(source: Concept, config: ArcGraphConfig) {
    val senseKeys = source.assignedSenseKeys.ifEmpty { source.senseKeyToGlossMap.keys }

    val defComponents = senseKeys.mapNotNull { senseKey ->
        source.senseKeyToGlossMap[senseKey]
            ?.toGraphComponent(
                config = config.copy(decompositionDepth = config.decompositionDepth - 1)
            )
    }
    //merge def Graphs with graph
    defComponents.map { it.graph }.merge()
        .let {
            vertexSet().forEach { concept ->
                if (!containsVertex(concept)) addVertex(concept)
            }
            edgeSet().forEach { edge ->
                val currSource = edge.source as Concept
                val currTarget = edge.target as Concept
                if (edge !in getAllEdges(currSource, currTarget)) {
                    addEdge(currSource, currTarget, edge)
                    setEdgeWeight(edge, edge.edgeWeight)
                }
            }
        }

    //link def Graph with source
    defComponents.map { it.conceptMap.values }.flatten().forEach { concept ->
        addSemanticRelationsRecursive(concept, config)
        createEdge(
            edgeType = EdgeType.Definition,
            source = source,
            target = concept
        )
            ?.let { edge ->
                if (edge !in getAllEdges(source, concept)) {
                    addEdge(source, concept, edge)
                    setEdgeWeight(edge, edge.edgeWeight)
                }
            }
    }
}

fun DefaultListenableGraph<Concept, WeightedEdge>.addConceptsBy(source: Concept, config: ArcGraphConfig) {
    val senseKeys = source.assignedSenseKeys.ifEmpty { source.senseKeyToGlossMap.keys }
    mapOf(
        EdgeType.Synonym to source.senseKeyToSynonymsMap,
        EdgeType.Antonym to source.senseKeyToAntonymsMap,
        EdgeType.Hyponym to source.senseKeyToHyponymsMap,
        EdgeType.Hypernym to source.senseKeyToHypernymsMap,
        EdgeType.Meronym to source.senseKeyToMeronymsMap
    )
        .mapValues { (_, senseKeyMap) ->
            senseKeys.mapNotNull { senseKey -> senseKeyMap[senseKey] }.flatten()
        }
        .map { (edgeType, conceptList) ->
            conceptList.map { con -> edgeType to con }
        }
        .flatten()
        .filterNot { (_, target) -> source == target || target in Decomposition.getConcepts2Ignore() }
        .forEach { (edgeType, target) ->
            addSemanticRelationsRecursive(target, config)
            createEdge(
                edgeType = edgeType,
                source = source,
                target = target
            )
                ?.let { edge ->
                    if (edge !in getAllEdges(source, target)) {
                        addEdge(source, target, edge)
                        setEdgeWeight(edge, edge.edgeWeight)
                    }
                }
        }
}

fun List<Graph<Concept, WeightedEdge>>.merge() =
    DefaultListenableGraph(
        DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
    ).also { graph ->
        map { it.vertexSet() }
            .flatten()
            .distinct()
            .forEach { graph.addVertex(it) }
        map { it.edgeSet() }
            .flatten()
            .distinct()
            .forEach { edge ->
                graph.addEdge(edge.source as Concept, edge.target as Concept, edge)
                graph.setEdgeWeight(edge, edge.edgeWeight)
            }
    }