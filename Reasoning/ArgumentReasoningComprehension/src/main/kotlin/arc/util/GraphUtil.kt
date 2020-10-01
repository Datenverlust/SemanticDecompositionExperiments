package arc.util

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

fun createEdge(
    edgeType: EdgeType,
    source: String,
    target: String,
    relation: String? = null
): WeightedEdge? {
    val edgeTypeAttribute = "edgeType"
    return when (edgeType) {
        EdgeType.Synonym -> createEdge(
            edge = SynonymEdge(),
            source = source,
            target = target,
            attributes = mapOf(edgeTypeAttribute to "synonym")
        )
        EdgeType.Definition -> createEdge(
            edge = DefinitionEdge(),
            source = source,
            target = target,
            attributes = mapOf(edgeTypeAttribute to "definition")
        )
        EdgeType.Hypernym -> createEdge(
            edge = HypernymEdge(),
            source = source,
            target = target,
            attributes = mapOf(edgeTypeAttribute to "hypernym")
        )
        EdgeType.Hyponym -> createEdge(
            edge = HyponymEdge(),
            source = source,
            target = target,
            attributes = mapOf(edgeTypeAttribute to "hyponym")
        )
        EdgeType.Meronym -> createEdge(
            edge = MeronymEdge(),
            source = source,
            target = target,
            attributes = mapOf(edgeTypeAttribute to "meronym")
        )
        EdgeType.Antonym -> createEdge(
            edge = AntonymEdge(),
            source = source,
            target = target,
            attributes = mapOf(edgeTypeAttribute to "antonym")
        )
        EdgeType.Syntax -> createEdge(
            edge = SyntaxEdge(),
            source = source,
            target = target,
            attributes = mapOf(
                edgeTypeAttribute to "syntax",
                "syntaxRelation" to (relation ?: "")
            )
        )
        EdgeType.NamedEntity -> createEdge(
            edge = NamedEntityEdge(),
            source = source,
            target = target,
            attributes = mapOf(
                edgeTypeAttribute to "namedEntity"
            )
        )
        EdgeType.SemanticRole -> createEdge(
            edge = SemanticRoleEdge(),
            source = source,
            target = target,
            attributes = mapOf(
                edgeTypeAttribute to "semanticRole"
            )
        )
        else -> null
    }
}

fun createEdge(edge: WeightedEdge, source: String, target: String, attributes: Map<String, String>): WeightedEdge {
    edge.source = source
    edge.target = target
    edge.attributes = attributes
    return edge
}

fun List<DefaultListenableGraph<String, WeightedEdge>>.merge() =
    DefaultListenableGraph(
        DefaultDirectedWeightedGraph<String, WeightedEdge>(WeightedEdge::class.java)
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
                    graph.addEdge(edge.source as String, edge.target as String, edge)
                    graph.setEdgeWeight(edge, edge.edgeWeight)
                }
            }
    }

fun DefaultListenableGraph<String, WeightedEdge>.addGraph(graph: DefaultListenableGraph<String, WeightedEdge>) {
    graph.vertexSet().forEach { node -> if (!containsVertex(node)) addVertex(node) }
    graph.edgeSet().forEach { edge ->
        if (!containsEdge(edge)) {
            addEdge(edge.source as String, edge.target as String, edge)
            setEdgeWeight(edge, edge.edgeWeight)
        }
    }
}