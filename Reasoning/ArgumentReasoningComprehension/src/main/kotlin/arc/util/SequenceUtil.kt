package arc.util

fun <T> Sequence<T>.mapIf(condition: Boolean, mapper: (T) -> T) = if (condition) map(mapper) else this

fun <T> Sequence<T>.printProgress(
    interval: Int,
    total: Int,
    msg: String? = null,
    printer: (String) -> Unit = { println(it) }
): Sequence<T> = sequence {
    val timer = TimerUtils()
    val totalDouble = total.toDouble()
    this@printProgress.forEachIndexed { index, t ->
        yield(t)
        val counter = index + 1
        if (counter % interval == 0 || counter == total) {
            val progress = counter / totalDouble
            printer.invoke(
                "%s  %,10d/%,d  %6.2f%%  %s%s".format(
                    timer.formattedDuration,
                    counter,
                    total,
                    progress * 100,
                    timer.getFormattedDurationEstimation(progress),
                    msg?.let { " $it" } ?: ""
                )
            )
        }
    }
}

fun <T> Sequence<T>.printProgress(
    interval: Int,
    msg: String? = null,
    printer: (String) -> Unit = { println(it) }
): Sequence<T> = sequence {
    val timer = TimerUtils()
    this@printProgress.forEachIndexed { index, t ->
        yield(t)
        val counter = index + 1
        if (counter % interval == 0) {
            printer.invoke(
                "%s  %,10d  %s".format(
                    timer.formattedDuration,
                    counter,
                    msg?.let { " $it" } ?: ""
                )
            )
        }
    }
}