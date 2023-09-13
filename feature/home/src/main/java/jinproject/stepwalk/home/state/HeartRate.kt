package jinproject.stepwalk.home.state

import android.util.Log
import jinproject.stepwalk.design.R
import java.time.Instant
import java.util.SortedMap

internal data class HeartRateMenu(
    val heartRates: List<HeartRate>
) : HealthMenu {
    override var details: MutableMap<String, MenuDetail>? = null
    override var graphItems: List<Long>? = null

    fun setGraphItems(time: Time) = kotlin.run {
        val items = ArrayList<Long>(time.toRepeatTimes()).apply {
            repeat(time.toRepeatTimes()) { index ->
                add(index, 0L)
            }
        }

        heartRates.forEach { heart ->
            val instant = heart.startTime
            val key = time.toZonedOffset(instant)
            when (time) {
                Time.Day -> items[key] = heart.avg.toLong()
                else -> items[key - 1] = heart.avg.toLong()
            }
        }

        graphItems = when(time) {
            Time.Week -> items.sortDayOfWeek()
            else -> items
        }
    }

    fun setMenuDetails() = kotlin.runCatching {

        details = mutableMapOf<String, MenuDetail>().apply {
            set(
                "min", MenuDetail(
                    value = heartRates.map { it.min }.average().toFloat(),
                    img = R.drawable.ic_heart_solid,
                    intro = "최소(분)"
                )
            )
            set(
                "avg", MenuDetail(
                    value = heartRates.map { it.avg }.average().toFloat(),
                    img = R.drawable.ic_heart_solid,
                    intro = "평균(분)"
                )
            )
            set(
                "max", MenuDetail(
                    value = heartRates.map { it.max }.average().toFloat(),
                    img = R.drawable.ic_heart_solid,
                    intro = "최대(분)"
                )
            )
        }
    }.onFailure { e ->
        Log.e("test", "error: ${e.message}")
    }

    companion object {
        fun getInitValues() = HeartRateMenu(
            heartRates = listOf(
                HeartRate(
                    startTime = Instant.now(),
                    endTime = Instant.now(),
                    min = 0,
                    max = 0,
                    avg = 0
                )
            )
        )
    }
}

data class HeartRate(
    val startTime: Instant,
    val endTime: Instant,
    val min: Int,
    val max: Int,
    val avg: Int
) {
    companion object {
        fun getInitValues() = HeartRate(
            startTime = Instant.now(),
            endTime = Instant.now(),
            min = 0,
            max = 0,
            avg = 0
        )
    }
}
