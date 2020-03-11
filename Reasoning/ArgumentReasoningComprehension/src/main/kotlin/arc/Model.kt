package arc

data class ArcTask(
    val id: String,
    val warrant0: String,
    val warrant1: String,
    val correctLabelW0orW1: Label,
    val reason: String,
    val claim: String,
    val debateTitle: String,
    val debateInfo: String
)

enum class Label {
    W0,
    W1,
    UNKNOWN
}