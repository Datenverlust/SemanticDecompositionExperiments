package arc.util

import arc.markerPassingConfig
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
import org.jgrapht.graph.DefaultDirectedWeightedGraph
import org.jgrapht.graph.DefaultListenableGraph

fun createEdge(edgeType: EdgeType, source: Concept, target: Concept, relation: String? = null): WeightedEdge? {
    val edgeTypeAttribute = "edgeType"
    return when (edgeType) {
        EdgeType.Synonym -> createEdge(
            edge = SynonymEdge(),
            source = source,
            target = target,
            weight = markerPassingConfig.synonymLinkWeight,
            attributes = mapOf(edgeTypeAttribute to "synonym")
        )
        EdgeType.Definition -> createEdge(
            edge = DefinitionEdge(),
            source = source,
            target = target,
            weight = markerPassingConfig.definitionLinkWeight,
            attributes = mapOf(edgeTypeAttribute to "definition")
        )
        EdgeType.Hypernym -> createEdge(
            edge = HypernymEdge(),
            source = source,
            target = target,
            weight = markerPassingConfig.hypernymLinkWeight,
            attributes = mapOf(edgeTypeAttribute to "hypernym")
        )
        EdgeType.Hyponym -> createEdge(
            edge = HyponymEdge(),
            source = source,
            target = target,
            weight = markerPassingConfig.hyponymLinkWeight,
            attributes = mapOf(edgeTypeAttribute to "hyponym")
        )
        EdgeType.Meronym -> createEdge(
            edge = MeronymEdge(),
            source = source,
            target = target,
            weight = markerPassingConfig.meronymLinkWeight,
            attributes = mapOf(edgeTypeAttribute to "meronym")
        )
        EdgeType.Antonym -> createEdge(
            edge = AntonymEdge(),
            source = source,
            target = target,
            weight = markerPassingConfig.antonymLinkWeight,
            attributes = mapOf(edgeTypeAttribute to "antonym")
        )
        EdgeType.Syntax -> createEdge(
            edge = SyntaxEdge(),
            source = source,
            target = target,
            weight = markerPassingConfig.syntaxLinkWeight,
            attributes = mapOf(
                edgeTypeAttribute to "syntax",
                "syntaxRelation" to (relation ?: "")
            )
        )
        EdgeType.NamedEntity -> createEdge(
            edge = NamedEntityEdge(),
            source = source,
            target = target,
            weight = markerPassingConfig.namedEntityLinkWeight,
            attributes = mapOf(
                edgeTypeAttribute to "namedEntity"
            )
        )
        EdgeType.SemanticRole -> createEdge(
            edge = SemanticRoleEdge(),
            source = source,
            target = target,
            weight = markerPassingConfig.semanticRoleLinkWeight,
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

fun List<DefaultListenableGraph<Concept, WeightedEdge>>.merge() =
    DefaultListenableGraph(
        DefaultDirectedWeightedGraph<Concept, WeightedEdge>(WeightedEdge::class.java)
    ).also { graph ->
        map { it.vertexSet() }
            .flatten()
            .distinct()
            .forEach { vertex -> if (!graph.containsVertex(vertex)) graph.addVertex(vertex) }
        map { it.edgeSet() }
            .flatten()
            .distinct()
            .forEach { edge ->
                if (!graph.containsEdge(edge)) {
                    graph.addEdge(edge.source as Concept, edge.target as Concept, edge)
                    graph.setEdgeWeight(edge, edge.edgeWeight)
                }
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