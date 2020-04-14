package arc.util

import org.joda.time.Period
import org.joda.time.format.PeriodFormatter
import org.joda.time.format.PeriodFormatterBuilder

class TimerUtils {
    private var startTime: Long = 0
    private var endTime: Long = 0
    fun start() {
        startTime = System.currentTimeMillis()
        endTime = -1
    }

    fun stop() {
        endTime = System.currentTimeMillis()
    }

    fun getFormattedDurationEstimation(progress: Double): String {
        return getFormattedDurationEstimation(formatter, progress)
    }

    fun getFormattedDurationEstimation(formatter: PeriodFormatter, progress: Double): String {
        val now = System.currentTimeMillis()
        val duration = now - startTime
        val expected = if (progress == 0.0) 0L else Math.round(duration / progress) - duration
        return formatter.print(Period(now, now + expected))
    }

    val formattedDuration: String
        get() = formatter.print(Period(startTime, System.currentTimeMillis()))

    companion object {
        private val formatter = PeriodFormatterBuilder()
            .printZeroAlways()
            .minimumPrintedDigits(2)
            .appendHours()
            .appendSeparator(":")
            .minimumPrintedDigits(2)
            .appendMinutes()
            .appendSeparator(":")
            .minimumPrintedDigits(2)
            .appendSeconds()
            .toFormatter()
    }

    init {
        start()
    }
}