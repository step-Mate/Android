package jinproject.stepwalk.home.state

import jinproject.stepwalk.home.utils.onKorea
import java.time.Instant
import java.time.LocalDate
import java.time.Period
import java.time.ZoneId
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAdjusters
import java.util.Locale

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
        val zonedDateTime = today.onKorea()
        when (this) {
            Day -> zonedDateTime.hour
            Week -> zonedDateTime.dayOfWeek.value
            Month -> zonedDateTime.dayOfMonth
            Year -> zonedDateTime.monthValue
        }
    }

    fun display() = when(this) {
        Year -> "올해"
        Month -> "이번달"
        Week -> "이번주"
        Day -> "오늘"
    }

    fun toPeriod(): Period = when(this) {
        Year -> Period.ofYears(1)
        Month -> Period.ofMonths(1)
        Week -> Period.ofWeeks(1)
        else -> throw IllegalStateException("Period 로 변환할수 없는 $this 입니다.")
    }

}

internal fun Int.weekToString() = LocalDate
    .now()
    .with(ChronoField.DAY_OF_WEEK,this.toLong() + 1)
    .dayOfWeek
    .getDisplayName(java.time.format.TextStyle.SHORT,Locale.getDefault())