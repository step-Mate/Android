package jinproject.stepwalk.home.screen.calendar.state

import android.util.Log
import androidx.compose.runtime.Stable
import jinproject.stepwalk.home.utils.onKorea
import java.time.LocalDateTime
import java.time.ZonedDateTime

/**
 * 시간 범위를 표현해주기 위해 Iterable을 구현한 클래스
 * @param time 날짜
 * @property compareTo step의 크기가 1인 ClosedRange를 구현하기 위해 필요한 Comparable 의 구현메소드
 * @property rangeTo (a..b) 에 대한 연산자 중복
 */
@Stable
internal class ZonedTime(val time: ZonedDateTime) : Comparable<ZonedTime> {

    override fun compareTo(other: ZonedTime): Int {
        return this.time.monthValue.compareTo(other.time.monthValue)
    }

    operator fun rangeTo(that: ZonedTime) = kotlin.runCatching {
        ZonedTimeRange(this, that)
    }.getOrElse { e ->
        Log.e("test", e.stackTraceToString())

        val time = LocalDateTime.now().onKorea()

        ZonedTimeRange(
            start = ZonedTime(time),
            endInclusive = ZonedTime(time)
        )
    }

    fun getMonths() = time.monthValue + (time.year - 1) * 12
}

/**
 * 1달 단위의 Step 으로 start ~ end 까지의 Range를 표현하는 클래스
 * @param start 시작 날짜
 * @param endInclusive 끝 날짜
 */
@Stable
internal class ZonedTimeRange(
    override val start: ZonedTime,
    override val endInclusive: ZonedTime,
) : Iterable<ZonedTime>, ClosedRange<ZonedTime> {
    override fun iterator(): Iterator<ZonedTime> {
        return if (start.time.toEpochSecond() > endInclusive.time.toEpochSecond())
            throw IllegalStateException("start: ${start.time.toEpochSecond()} 는 last: ${endInclusive.time.toEpochSecond()} 보다 작거나 같아야함")
        else
            ZonedTimeIterator(start, endInclusive)
    }

    override fun contains(value: ZonedTime): Boolean {
        return (value.getMonths() >= start.getMonths() && value.getMonths() <= endInclusive.getMonths())
    }

    override fun toString(): String {
        return StringBuilder("ZonedTimeRange: {").apply {
            for (i in start..endInclusive) {
                if (i != start)
                    append(",")
                append("ZonedTime(${i.time})")
                if (i != endInclusive)
                    append(" ")
            }
            append("}")
        }.toString()
    }

}

/**
 * ZonedTimeRange의 Iterator의 구현체로, 현재값을 프로퍼티로 가지고 다음값의 여부와 다음값을 가져오는 메소드가 있음.
 * @param start 시작 날짜
 * @param endInclusive 끝 날짜
 */
internal class ZonedTimeIterator(
    start: ZonedTime,
    private val endInclusive: ZonedTime,
) : Iterator<ZonedTime> {
    private var initValue: ZonedTime = start

    override fun hasNext(): Boolean {
        return initValue.time.toEpochSecond() <= endInclusive.time.toEpochSecond()
    }

    override fun next(): ZonedTime {
        return initValue.apply {
            initValue = ZonedTime(initValue.time.plusMonths(1L))
        }
    }
}