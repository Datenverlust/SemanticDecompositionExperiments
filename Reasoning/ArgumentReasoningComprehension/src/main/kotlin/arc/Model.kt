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

fun ArcTask.getConceptElements() =
    listOf(this.warrant0, this.warrant1, this.reason, this.claim, this.debateTitle, this.debateInfo)

enum class Label {
    W0,
    W1,
    UNKNOWN
}