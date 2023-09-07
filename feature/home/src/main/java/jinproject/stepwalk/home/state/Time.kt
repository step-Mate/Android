package jinproject.stepwalk.home.state

import androidx.compose.ui.text.TextStyle
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAdjusters
import java.time.temporal.WeekFields
import java.util.Locale
import javax.annotation.meta.When

internal enum class Time {
    Day,
    Week,
    Month,
    Year;

    fun toRepeatTimes() = when (this) {
        Day -> 24
        Week -> 7
        Month -> Instant
            .now()
            .atZone(ZoneId.of("Asia/Seoul"))
            .with(TemporalAdjusters.lastDayOfMonth())
            .get(ChronoField.DAY_OF_MONTH)

        Year -> 12
    }

    fun toZonedOffset(today: Instant) = run {
        val zonedDateTime = today.atZone(ZoneId.of("Asia/Seoul"))
        when (this) {
            Day -> zonedDateTime.hour
            Week -> zonedDateTime.dayOfWeek.value
            Month -> zonedDateTime.monthValue
            Year -> zonedDateTime.year
        }
    }

    fun display() = when(this) {
        Year -> "년"
        Month -> "달"
        Week -> "주"
        Day -> "일"
    }
}

internal fun Int.weekToString() = LocalDate
    .now()
    .with(ChronoField.DAY_OF_WEEK,this.toLong() + 1)
    .dayOfWeek
    .getDisplayName(java.time.format.TextStyle.SHORT,Locale.getDefault())