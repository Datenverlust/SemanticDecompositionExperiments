package arc

data class ArcTask(
    val id: String,
    val warrant0: String,
    val warrant1: String,
    val correctLabelW0orW1: ArcLabel,
    val reason: String,
    val claim: String,
    val debateTitle: String,
    val debateInfo: String
)

data class ArcComponents(
    val graphComponents: Collection<String>,
    val startActivationComponents: Collection<String>,
    val thresholdComponents: Collection<String>,
    val warrant0Components: Collection<String>,
    val warrant1Components: Collection<String>,
    val evaluationComponents: Collection<String>
)

enum class ArcLabel {
    W0,
    W1,
    UNKNOWN
}