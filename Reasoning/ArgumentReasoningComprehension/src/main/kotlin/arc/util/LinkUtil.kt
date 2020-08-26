package arc.util

import de.kimanufaktur.markerpassing.Link
import de.kimanufaktur.markerpassing.Node
import de.kimanufaktur.nsm.decomposition.graph.edges.EdgeType
import de.kimanufaktur.nsm.decomposition.graph.edges.WeightedEdge
import de.kimanufaktur.nsm.graph.entities.links.AntonymLink
import de.kimanufaktur.nsm.graph.entities.links.DefinitionLink
import de.kimanufaktur.nsm.graph.entities.links.HypernymLink
import de.kimanufaktur.nsm.graph.entities.links.HyponymLink
import de.kimanufaktur.nsm.graph.entities.links.MeronymLink
import de.kimanufaktur.nsm.graph.entities.links.NamedEntityLink
import de.kimanufaktur.nsm.graph.entities.links.SemanticRoleLink
import de.kimanufaktur.nsm.graph.entities.links.SynonymLink
import de.kimanufaktur.nsm.graph.entities.links.SyntaxLink

fun WeightedEdge.toEmptyLink() = when (edgeType) {
    EdgeType.Synonym -> SynonymLink()
    EdgeType.Antonym -> AntonymLink()
    EdgeType.Hyponym -> HyponymLink()
    EdgeType.Hypernym -> HypernymLink()
    EdgeType.Meronym -> MeronymLink()
    EdgeType.Definition -> DefinitionLink()
    EdgeType.Syntax -> SyntaxLink()
    EdgeType.NamedEntity -> NamedEntityLink()
    EdgeType.SemanticRole -> SemanticRoleLink()
    else -> null
}

fun Link.reverseByType(edgeType: EdgeType) = when (edgeType) {
    EdgeType.Hyponym -> HypernymLink().reverse(source, target)
    EdgeType.Hypernym -> HyponymLink().reverse(source, target)
    EdgeType.SemanticRole -> null
    EdgeType.Definition -> null
    else -> reverse()
}

private fun Link.reverse() = apply {
    source = this@reverse.target
    target = this@reverse.source
}

private fun Link.reverse(source: Node, target: Node) = apply {
    this.source = target
    this.target = source
}
