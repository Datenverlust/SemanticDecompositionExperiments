package arc

import de.kimanufaktur.nsm.decomposition.graph.spreadingActivation.MarkerPassing.MarkerPassingConfig

val decompositionDepth = MarkerPassingConfig.getDecompositionDepth()

val startActivation = MarkerPassingConfig.getStartActivation()
val threshold = MarkerPassingConfig.getThreshold()

val synonymLinkWeight = MarkerPassingConfig.getSynonymLinkWeight()
val definitionLinkWeight = MarkerPassingConfig.getDefinitionLinkWeight()
val antonymLinkWeight = MarkerPassingConfig.getAntonymLinkWeight()
val meronymLinkWeight = MarkerPassingConfig.getMeronymLinkWeight()
val hyponymLinkWeight = MarkerPassingConfig.getHyponymLinkWeight()
val hypernymLinkWeight = MarkerPassingConfig.getHypernymLinkWeight()
val syntaxLinkWeight = MarkerPassingConfig.getSyntaxLinkWeight()
val namedEntityLinkWeight = MarkerPassingConfig.getNerLinkWeight()
val semanticRoleLinkWeight = MarkerPassingConfig.getRoleLinkWeight()

