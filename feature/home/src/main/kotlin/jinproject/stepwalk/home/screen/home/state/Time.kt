package jinproject.stepwalk.home.screen.home.state

import androidx.compose.runtime.Stable
import jinproject.stepwalk.design.component.layout.chart.sortDayOfWeek
import jinproject.stepwalk.home.utils.onKorea
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

@Stable
internal sealed interface Time {
    fun toNumberOfDays(): Int
    fun getSeparateUnit(zonedDateTime: ZonedDateTime): Int
    fun display(): String
    fun toPeriod(): Period

    companion object {
        val values get() = listOf(Day, Week, Month, Year)
    }
}

@Stable
internal data object Day : Time {
    override fun toNumberOfDays(): Int = 24
    override fun getSeparateUnit(zonedDateTime: ZonedDateTime): Int = zonedDateTime.hour
    override fun display(): String = "오늘"
    override fun toPeriod(): Period = throw IllegalArgumentException("변환 불가")
}

@Stable
internal data object Week : Time {
    override fun toNumberOfDays(): Int = 7
    override fun getSeparateUnit(zonedDateTime: ZonedDateTime): Int = zonedDateTime.dayOfWeek.value
    override fun display(): String = "이번주"
    override fun toPeriod(): Period = Period.ofDays(1)
}

@Stable
internal data object Month : Time {
    override fun toNumberOfDays(): Int = Instant
        .now()
        .onKorea()
        .with(TemporalAdjusters.lastDayOfMonth())
        .get(ChronoField.DAY_OF_MONTH)

    override fun getSeparateUnit(zonedDateTime: ZonedDateTime): Int = zonedDateTime.dayOfMonth
    override fun display(): String = "이번달"
    override fun toPeriod(): Period = Period.ofDays(1)
}

@Stable
internal data object Year : Time {
    override fun toNumberOfDays(): Int = 12
    override fun getSeparateUnit(zonedDateTime: ZonedDateTime): Int = zonedDateTime.monthValue
    override fun display(): String = "올해"
    override fun toPeriod(): Period = Period.ofMonths(1)
}

internal fun <T : HealthCare> Time.getGraph(list: List<T>): List<Long> {
    val dayCount = this.toNumberOfDays()
    val items = HealthTab.getDefaultGraphItems(dayCount)

    list.forEach { item ->
        val startTime = item.startTime
        val value = item.figure

        val key = this.getSeparateUnit(startTime)

        when (this) {
            Day -> items[key] = value
            else -> items[key - 1] = value
        }
    }

    if (this is Week) {
        return items.sortDayOfWeek()
    }

    return items.toList()
}

/**
 * Time object 에 따라 시작시간을 계산하여 반환하는 함수
 * @param startTime 시작 일자
 */
internal fun Time.getStartTime(
    startTime: ZonedDateTime = Instant.now().onKorea(),
) =
    when (this) {
        /**
         * 1월 1일 ~
         */
        Year -> startTime
            .minusMonths(startTime.month.value.toLong() - 1)
            .minusDays(startTime.dayOfMonth.toLong() - 1)

        /**
         * 6일전 ~
         */
        Week -> startTime
            .minusDays(toNumberOfDays().toLong() - 1)

        /**
         * 이번달 1일 ~
         */
        Month -> startTime.minusDays(startTime.dayOfMonth.toLong() - 1)

        Day -> throw IllegalArgumentException("")
    }
        .truncatedTo(ChronoUnit.DAYS)
