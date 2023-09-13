package jinproject.stepwalk.home.state

import jinproject.stepwalk.home.utils.onKorea
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
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
            .onKorea()
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

    fun display() = when (this) {
        Year -> "올해"
        Month -> "이번달"
        Week -> "이번주"
        Day -> "오늘"
    }

    fun toPeriod(): Period = when (this) {
        Year -> Period.ofMonths(1)
        Month -> Period.ofDays(1)
        Week -> Period.ofDays(1)
        else -> throw IllegalStateException("Period 로 변환할수 없는 $this 입니다.")
    }

}

/**
 * 이번주에서 오늘이 가장 마지막에 위치하도록 sort 하는 함수
 */
internal fun List<Long>.sortDayOfWeek() = run {
    val today = LocalDateTime.now().onKorea().dayOfWeek.value
    val arrayList = ArrayList<Long>(7)

    val subListBigger = this.filterIndexed { index, _ -> index + 1 > today }
    val subListSmaller = this.filterIndexed { index, _ -> index + 1 <= today }

    arrayList.apply {
        addAll(subListBigger)
        addAll(subListSmaller)
    }
}

internal fun Long.weekToString() = kotlin.run {
    val week = LocalDate
        .now()
        .with(ChronoField.DAY_OF_WEEK, this + 1)
        .dayOfWeek

    when (week) {
        LocalDate.now().dayOfWeek -> "오늘"
        else -> week.getDisplayName(java.time.format.TextStyle.SHORT, Locale.getDefault())
    }
}