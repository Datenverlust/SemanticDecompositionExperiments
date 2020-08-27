package arc.util

import arc.ArcConfig
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

fun WeightedEdge.toEmptyLink(config: ArcConfig) = when (edgeType) {
    EdgeType.Synonym -> SynonymLink().also { it.weight = config.synonymLinkWeight }
    EdgeType.Antonym -> AntonymLink().also { it.weight = config.antonymLinkWeight }
    EdgeType.Hyponym -> HyponymLink().also { it.weight = config.hyponymLinkWeight}
    EdgeType.Hypernym -> HypernymLink().also { it.weight = config.hypernymLinkWeight}
    EdgeType.Meronym -> MeronymLink().also { it.weight = config.meronymLinkWeight}
    EdgeType.Definition -> DefinitionLink().also { it.weight = config.definitionLinkWeight}
    EdgeType.Syntax -> SyntaxLink().also { it.weight = config.syntaxLinkWeight}
    EdgeType.NamedEntity -> NamedEntityLink().also { it.weight = config.namedEntityLinkWeight}
    EdgeType.SemanticRole -> SemanticRoleLink().also { it.weight = config.semanticRoleLinkWeight}
    else -> null
}

fun Link.reverseByType(edgeType: EdgeType, config: ArcConfig) = when (edgeType) {
    EdgeType.SemanticRole, EdgeType.Definition, EdgeType.Meronym -> null
    EdgeType.Synonym -> SynonymLink().also { it.weight = config.synonymLinkWeight }.reverse(source, target)
    EdgeType.Antonym -> AntonymLink().also { it.weight = config.antonymLinkWeight }.reverse(source, target)
    EdgeType.Syntax -> SyntaxLink().also { it.weight = config.syntaxLinkWeight }.reverse(source, target)
    EdgeType.NamedEntity -> NamedEntityLink().also { it.weight = config.namedEntityLinkWeight }.reverse(source, target)
    EdgeType.Hyponym -> HypernymLink().also { it.weight = config.hypernymLinkWeight }.reverse(source, target)
    EdgeType.Hypernym -> HyponymLink().also { it.weight = config.hyponymLinkWeight }.reverse(source, target)
    else -> null
}

private fun Link.reverse(source: Node, target: Node) = apply {
    this.source = target
    this.target = source
}
