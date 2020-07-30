package arc

var defaultDecompositionDepth = 2

var startActivation = 100.0
var threshold = 0.1

var synonymLinkWeight = 0.9
var definitionLinkWeight = 0.8
var antonymLinkWeight = -0.9
var hyponymLinkWeight = 0.5
var hypernymLinkWeight = 0.5
var meronymLinkWeight = 0.5
var syntaxLinkWeight = 0.8
var namedEntityLinkWeight = 1.0
var semanticRoleLinkWeight = 1.0

var useSemanticDecomposition = true
var useSyntaxDependencies = true
var useSemanticRoles = false
var useNamedEntities = true
var useWsd = false
var useNegationHandling = true
