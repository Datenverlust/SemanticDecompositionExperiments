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
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultListenableGraph
import java.util.Collections

fun Concept.asKey(config: ArcGraphConfig) = "${hashCode()}#${config.hashCode()}"
fun CoreDocument.asKey(config: ArcGraphConfig) = "${text()}#${config.hashCode()}"

val semanticGraphCache: MutableMap<String, DefaultListenableGraph<Concept, WeightedEdge>> =
    Collections.synchronizedMap<String, DefaultListenableGraph<Concept, WeightedEdge>>(mutableMapOf())

val rolesGraphCache: MutableMap<String, DefaultListenableGraph<Concept, WeightedEdge>> =
    Collections.synchronizedMap<String, DefaultListenableGraph<Concept, WeightedEdge>>(mutableMapOf())

val syntaxGraphCache: MutableMap<String, DefaultListenableGraph<Concept, WeightedEdge>> =
    Collections.synchronizedMap<String, DefaultListenableGraph<Concept, WeightedEdge>>(mutableMapOf())

fun DefaultListenableGraph<Concept, WeightedEdge>.addSemanticGraph(
    conceptList: Collection<Concept>,
    config: ArcGraphConfig
) {
    conceptList.map { source ->
        val key = source.asKey(config)
        semanticGraphCache[key] ?: DefaultListenableGraph(
            DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
        )
            .also { graph -> graph.addSemanticRelationsRecursive(source, config) }
            .also { semanticGraphCache[key] = it }
    }
        .merge()
        .also { graph -> addGraph(graph) }
}


fun DefaultListenableGraph<Concept, WeightedEdge>.addSyntaxGraph(
    conceptMap: Map<CoreLabel, Concept>,
    coreDoc: CoreDocument,
    config: ArcGraphConfig
) = syntaxGraphCache[coreDoc.asKey(config)]
    ?: DefaultListenableGraph(
        DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
    ).also { graph ->
        if (config.depth <= 0) return@also
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
                                    if (!graph.containsEdge(edge)) {
                                        graph.addEdge(source, target, edge)
                                        graph.setEdgeWeight(edge, edge.edgeWeight)
                                    }
                                }
                            }
                    }

            }
    }
        .also { graph -> syntaxGraphCache[coreDoc.asKey(config)] = graph }


fun DefaultListenableGraph<Concept, WeightedEdge>.addNerGraph(conceptMap: Map<CoreLabel, Concept>, config: ArcGraphConfig) =
    DefaultListenableGraph(DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java))
        .also { graph ->
            conceptMap
                .filterKeys { it.ner() != null && it.ner() != "O" }
                .forEach { (nameCoreLabel, nameConcept) ->
                    graph.addVertex(nameConcept)
                    nameCoreLabel.ner().toLowerCase().decompose(config, nameCoreLabel.tag())
                        .also { entityConcept ->
                            graph.addSemanticRelationsRecursive(entityConcept, config)
                        }
                        .let { entityConcept ->
                            createEdge(
                                edgeType = EdgeType.NamedEntity,
                                source = nameConcept,
                                target = entityConcept
                            )?.let { edge ->
                                if (!graph.containsEdge(edge)) {
                                    graph.addEdge(nameConcept, entityConcept, edge)
                                    graph.setEdgeWeight(edge, edge.edgeWeight)
                                }
                            }
                        }
                }
        }

fun DefaultListenableGraph<Concept, WeightedEdge>.addRolesGraph(
    conceptMap: Map<CoreLabel, Concept>,
    coreDoc: CoreDocument,
    config: ArcGraphConfig
) = rolesGraphCache[coreDoc.asKey(config)]
    ?: DefaultListenableGraph(DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java))
        .also { graph ->
            if (config.depth <= 0) return@also
            identifySemanticRoles(coreDoc)
                .forEach { (coreLabel, roleList) ->
                    val source = conceptMap.getValue(coreLabel)
                        .also { graph.addVertex(it) }
                    roleList
                        .map { role ->
                            role.toGraphComponent(
                                config.copy(
                                    useSrl = false,
                                    depth = config.depth.dec()
                                )
                            ).graph
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
                                        if (!graph.containsEdge(edge)) {
                                            graph.addEdge(source, roleConcept, edge)
                                            graph.setEdgeWeight(edge, edge.edgeWeight)
                                        }
                                    }
                                }
                        }
                }
        }
        .also { graph -> rolesGraphCache[coreDoc.asKey(config)] }

fun createEdge(edgeType: EdgeType, source: Concept, target: Concept, syntaxRelation: String? = null): WeightedEdge? {
    val edgeTypeAttribute = "edgeType"
    return when (edgeType) {
        EdgeType.Synonym -> createEdge(
            edge = SynonymEdge(),
            source = source,
            target = target,
            weight = synonymLinkWeight,
            attributes = mapOf(edgeTypeAttribute to "synonym")
        )
        EdgeType.Definition -> createEdge(
            edge = DefinitionEdge(),
            source = source,
            target = target,
            weight = definitionLinkWeight,
            attributes = mapOf(edgeTypeAttribute to "definition")
        )
        EdgeType.Hypernym -> createEdge(
            edge = HypernymEdge(),
            source = source,
            target = target,
            weight = hypernymLinkWeight,
            attributes = mapOf(edgeTypeAttribute to "hypernym")
        )
        EdgeType.Hyponym -> createEdge(
            edge = HyponymEdge(),
            source = source,
            target = target,
            weight = hyponymLinkWeight,
            attributes = mapOf(edgeTypeAttribute to "hyponym")
        )
        EdgeType.Meronym -> createEdge(
            edge = MeronymEdge(),
            source = source,
            target = target,
            weight = meronymLinkWeight,
            attributes = mapOf(edgeTypeAttribute to "meronym")
        )
        EdgeType.Antonym -> createEdge(
            edge = AntonymEdge(),
            source = source,
            target = target,
            weight = antonymLinkWeight,
            attributes = mapOf(edgeTypeAttribute to "antonym")
        )
        EdgeType.Syntax -> createEdge(
            edge = SyntaxEdge(),
            source = source,
            target = target,
            weight = syntaxLinkWeight,
            attributes = mapOf(
                edgeTypeAttribute to "syntax",
                "syntaxRelation" to (syntaxRelation ?: "")
            )
        )
        EdgeType.NamedEntity -> createEdge(
            edge = NamedEntityEdge(),
            source = source,
            target = target,
            weight = namedEntityLinkWeight,
            attributes = mapOf(
                edgeTypeAttribute to "namedEntity"
            )
        )
        EdgeType.SemanticRole -> createEdge(
            edge = SemanticRoleEdge(),
            source = source,
            target = target,
            weight = semanticRoleLinkWeight,
            attributes = mapOf(
                edgeTypeAttribute to "semanticRole"
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
    if (!source.isStopWord() && !containsVertex(source)) {
        addVertex(source)
        addDefinitionsBy(source, config)
        addConceptsBy(source, config)
    }
}

fun DefaultListenableGraph<Concept, WeightedEdge>.addDefinitionsBy(source: Concept, config: ArcGraphConfig) {
    if (source.assignedSenseKeys.isEmpty()) {
        source.definitions
    } else {
        source.definitions.filter { def -> def.sensekey in source.assignedSenseKeys }
    }
        .mapNotNull { def ->
            def.gloss?.toGraphComponent(
                config = config.copy(depth = config.depth.dec())
            )
        }
        .also { defComponents ->
            addGraph(defComponents.map { component -> component.graph }.merge())
            defComponents.map { it.conceptMap.values }
                .flatten()
                .distinct()
                .forEach { target ->
                    if (!target.isStopWord()) {
                        createEdge(
                            edgeType = EdgeType.Definition,
                            source = source,
                            target = target
                        )
                            ?.let { edge ->
                                if (!containsEdge(edge)) {
                                    addEdge(source, target, edge)
                                    setEdgeWeight(edge, edge.edgeWeight)
                                }
                            }
                    }
                }
        }
}

fun DefaultListenableGraph<Concept, WeightedEdge>.addConceptsBy(source: Concept, config: ArcGraphConfig) {
    mapOf(
        EdgeType.Synonym to source.senseKeyToSynonymsMap,
        EdgeType.Antonym to source.senseKeyToAntonymsMap,
        EdgeType.Hyponym to source.senseKeyToHyponymsMap,
        EdgeType.Hypernym to source.senseKeyToHypernymsMap,
        EdgeType.Meronym to source.senseKeyToMeronymsMap
    )
        .mapValues { (_, senseKeyMap) ->
            if (source.assignedSenseKeys.isEmpty()) senseKeyMap.values.flatten()
            else source.assignedSenseKeys.mapNotNull { senseKey -> senseKeyMap[senseKey] }.flatten()
        }
        .map { (edgeType, conceptList) ->
            conceptList.map { con -> edgeType to con }
        }
        .flatten()
        .filterNot { (_, target) -> source == target || target.isStopWord() }
        .forEach { (edgeType, target) ->
            if (!target.isStopWord()) {
                addSemanticRelationsRecursive(target, config.copy(depth = config.depth.dec()))
                createEdge(
                    edgeType = edgeType,
                    source = source,
                    target = target
                )
                    ?.let { edge ->
                        if (!containsEdge(edge)) {
                            addEdge(source, target, edge)
                            setEdgeWeight(edge, edge.edgeWeight)
                        }
                    }
            }
        }
}

fun List<DefaultListenableGraph<Concept, WeightedEdge>>.merge() =
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

fun DefaultListenableGraph<Concept, WeightedEdge>.addGraph(graph: DefaultListenableGraph<Concept, WeightedEdge>) {
    graph.vertexSet().forEach { concept -> if (!containsVertex(concept)) addVertex(concept) }
    graph.edgeSet().forEach { edge ->
        if (!containsEdge(edge)) {
            addEdge(edge.source as Concept, edge.target as Concept, edge)
            setEdgeWeight(edge, edge.edgeWeight)
        }
    }
}