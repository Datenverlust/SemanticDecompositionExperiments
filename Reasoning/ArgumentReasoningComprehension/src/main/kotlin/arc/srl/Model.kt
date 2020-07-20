package arc.srl

data class Frameset(
    val predicate: List<Predicate>? = null
)

data class Predicate(
    val lemma: String? = null,
    val roleset: List<RoleSet>? = null
)

data class RoleSet(
    val id: String? = null,
    val name: String? = null,
    val roles: Roles? = null
)

data class Roles(
    val role: List<Role>? = null
)

data class Role(
    val descr: String? = null,
    val f: String? = null,
    val n: String? = null,
    val vnRole: VnRole? = null
)

data class VnRole(
    val vntheta: String? = null
)