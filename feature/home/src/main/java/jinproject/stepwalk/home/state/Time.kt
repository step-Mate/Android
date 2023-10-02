package jinproject.stepwalk.home.state

import jinproject.stepwalk.home.utils.onKorea
import java.time.Instant
import java.time.LocalDateTime
import java.time.Period
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAdjusters

internal enum class Time {
    Day,
    Week,
    Month,
    Year;

    fun toNumberOfDays() = when (this) {
        Day -> 24
        Week -> 7
        Month -> Instant
            .now()
            .onKorea()
            .with(TemporalAdjusters.lastDayOfMonth())
            .get(ChronoField.DAY_OF_MONTH)

        Year -> 12
    }

    fun toZonedOffset(today: ZonedDateTime) = when (this) {
        Day -> today.hour
        Week -> today.dayOfWeek.value
        Month -> today.dayOfMonth
        Year -> today.monthValue
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
 * 이번주에서 오늘이 가장 마지막에 위치하도록 값들을 sort 하는 함수
 *
 * *반드시 주단위로 정렬된 상태이어야 함
 * @exception IllegalArgumentException : 리스트가 비어있거나, 크기가 7을 초과하는 경우
 * @return 오늘이 가장 마지막인 7개의 요일 리스트
 */
internal fun List<Long>.sortDayOfWeek() = run {
    if (this.size > 7 || this.isEmpty())
        throw IllegalArgumentException("비어있는 리스트 이거나 size가 7을 초과함")

    val today = LocalDateTime.now().onKorea().dayOfWeek.value
    val arrayList = ArrayList<Long>(7)

    val subListBigger = this.filterIndexed { index, _ -> index + 1 > today }
    val subListSmaller = this.filterIndexed { index, _ -> index + 1 <= today }

    arrayList.apply {
        addAll(subListBigger)
        addAll(subListSmaller)
    }
}

/**
 * 그래프의 아이템들을 가져오는 함수
 * @param addData : 주어진 Time 에 따라 ArrayList에 add하는 람다
 * @return 년/월/주/일 단위의 그래프에 맞는 아이템 리스트를 반환
 */
internal inline fun Time.getGraphItems(addData: (Time, ArrayList<Long>) -> Unit): ArrayList<Long> = kotlin.run {
    val dayCount = this.toNumberOfDays()
    val items = ArrayList<Long>(dayCount).apply {
        repeat(dayCount) { index ->
            add(index, 0L)
        }
    }

    addData(this, items)

    when (this) {
        Time.Week -> items.sortDayOfWeek()
        else -> items
    }
}