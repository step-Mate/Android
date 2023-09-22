package jinproject.stepwalk.home.state

import java.time.ZonedDateTime

internal class ZonedTime(val time: ZonedDateTime): Comparable<ZonedTime> {

    override fun compareTo(other: ZonedTime): Int {
        return this.time.monthValue.compareTo(other.time.monthValue)
    }

    operator fun rangeTo(that: ZonedTime) = ZonedTimeRange(this,that)
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
    private var initValue: ZonedTime = start

    override fun hasNext(): Boolean {
        return initValue.time.monthValue <= endInclusive.time.monthValue
    }

    override fun next(): ZonedTime {
        return initValue.apply {
            initValue = ZonedTime(initValue.time.plusMonths(1L))
        }
    }
}