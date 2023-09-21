package jinproject.stepwalk.home.state

import java.time.ZonedDateTime

internal class ZonedTime(val time: ZonedDateTime): Comparable<ZonedTime> {

    override fun compareTo(other: ZonedTime): Int {
        return this.time.monthValue.compareTo(other.time.monthValue)
    }

    operator fun rangeTo(that: ZonedTime) = ZonedTimeRange(this,that)

    operator fun inc(): ZonedTime {
        return ZonedTime(time.plusMonths(1L))
    }
}

internal class ZonedTimeRange(
    override val start: ZonedTime,
    override val endInclusive: ZonedTime
): Iterable<ZonedTime>, ClosedRange<ZonedTime> {
    override fun iterator(): Iterator<ZonedTime> {
        return if(start.time.toEpochSecond() > endInclusive.time.toEpochSecond())
            throw IllegalStateException("start: ${start.time.toEpochSecond()} 는 last: ${endInclusive.time.toEpochSecond()} 보다 작거나 같아야함")
        else
            ZonedTimeIterator(start, endInclusive)
    }

}

internal class ZonedTimeIterator(
    start: ZonedTime,
    private val endInclusive: ZonedTime,
): Iterator<ZonedTime> {
    private var initValue = start

    override fun hasNext(): Boolean {
        return initValue.time.toEpochSecond() <= endInclusive.time.toEpochSecond()
    }

    override fun next(): ZonedTime {
        return initValue++
    }
}