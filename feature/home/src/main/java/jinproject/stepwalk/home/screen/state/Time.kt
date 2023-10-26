package jinproject.stepwalk.home.screen.state

import androidx.compose.runtime.Stable
import jinproject.stepwalk.home.utils.onKorea
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAdjusters

@Stable
internal sealed interface Time {
    fun toNumberOfDays(): Int
    fun toZonedOffset(zonedDateTime: ZonedDateTime): Int
    fun display(): String
    fun toPeriod(): Period

    companion object {
        val values get() = listOf(Day, Week, Month, Year)
    }
}

@Stable
internal data object Day : Time {
    override fun toNumberOfDays(): Int = 24
    override fun toZonedOffset(zonedDateTime: ZonedDateTime): Int = zonedDateTime.hour
    override fun display(): String = "오늘"
    override fun toPeriod(): Period = throw IllegalArgumentException("변환 불가")
}

@Stable
internal data object Week : Time {
    override fun toNumberOfDays(): Int = 7
    override fun toZonedOffset(zonedDateTime: ZonedDateTime): Int = zonedDateTime.dayOfWeek.value
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

    override fun toZonedOffset(zonedDateTime: ZonedDateTime): Int = zonedDateTime.dayOfMonth
    override fun display(): String = "이번달"
    override fun toPeriod(): Period = Period.ofDays(1)
}

@Stable
internal data object Year : Time {
    override fun toNumberOfDays(): Int = 12
    override fun toZonedOffset(zonedDateTime: ZonedDateTime): Int = zonedDateTime.monthValue
    override fun display(): String = "올해"
    override fun toPeriod(): Period = Period.ofMonths(1)
}

internal fun <T : HealthCare> Time.getGraph(list: List<T>): List<Long> {
    val dayCount = this.toNumberOfDays()
    val items = HealthTab.getDefaultGraphItems(dayCount)

    list.forEach { item ->
        val startTime = item.startTime
        val value = item.figure

        val instant = Instant.ofEpochSecond(startTime).onKorea()
        val key = this.toZonedOffset(instant)

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
 * 이번주에서 오늘이 가장 마지막에 위치하도록 값들을 sort 하는 함수
 *
 * *반드시 주단위로 정렬된 상태이어야 함
 * @exception IllegalArgumentException : 리스트가 비어있거나, 크기가 7을 초과하는 경우
 * @return 오늘이 가장 마지막인 7개의 요일 리스트
 */
internal fun <T : Number> List<T>.sortDayOfWeek() = run {
    if (this.size > 7 || this.isEmpty())
        throw IllegalArgumentException("비어있는 리스트 이거나 size가 7을 초과함")

    val today = LocalDateTime.now().onKorea().dayOfWeek.value
    val arrayList = ArrayList<T>(7)

    val subListBigger = this.filterIndexed { index, _ -> index + 1 > today }
    val subListSmaller = this.filterIndexed { index, _ -> index + 1 <= today }

    arrayList.apply {
        addAll(subListBigger)
        addAll(subListSmaller)
    }
}