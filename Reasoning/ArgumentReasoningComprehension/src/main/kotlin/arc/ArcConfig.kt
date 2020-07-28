package arc

val decompositionDepth = 1

val startActivation = 100
val threshold = 0.1

val synonymLinkWeight = 0.9
val definitionLinkWeight = 0.8
val antonymLinkWeight = -0.9
val hyponymLinkWeight = 0.5
val hypernymLinkWeight = 0.5
val meronymLinkWeight = 0.5
val syntaxLinkWeight = 0.8
val namedEntityLinkWeight = 1.0
val semanticRoleLinkWeight = 1.0

val useSemanticDecomposition = true
val useSyntaxDependencies = true
val useSemanticRoles = true
val useNamedEntities = true
val useWsd = true
val useNegationHandling = true
