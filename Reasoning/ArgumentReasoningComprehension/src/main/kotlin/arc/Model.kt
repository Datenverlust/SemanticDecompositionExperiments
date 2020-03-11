package arc

data class ArcTask(
    val id: String,
    val warrant0: String,
    val warrant1: String,
    val correctLabelW0orW1: String,
    val reason: String,
    val claim: String,
    val debateTitle: String,
    val debateInfo: String
)