package jinproject.stepwalk.home.state

import android.util.Log
import jinproject.stepwalk.design.R
import java.time.Instant
import java.time.ZoneId
import java.util.SortedMap

internal data class HeartRateMenu(
    val heartRates: List<HeartRate>
): HealthMenu {
    override var details: MutableMap<String, MenuDetail>? = null
    override var graphItems: SortedMap<Int, Long>? = null

    fun setGraphItems(time: Time) = kotlin.run {
        val items = mutableMapOf<Int, Long>().apply {
            repeat(time.toRepeatTimes()) { hour ->
                this[hour] = 0
            }
        }

        heartRates.forEach { heart ->
            val instant = heart.time
            val key = time.toZonedOffset(instant)
            items[key] = (items[key] ?: 0) + heart.perMinutes
        }

        graphItems = items.toSortedMap(compareBy { it })
    }

    fun setMenuDetails() = kotlin.runCatching {
        val hearts = heartRates.map { it.perMinutes }

        details = mutableMapOf<String, MenuDetail>().apply {
            set(
                "min", MenuDetail(
                    value = hearts.minOrNull()?.toFloat() ?: 0f,
                    img = R.drawable.ic_heart_solid,
                    intro = "최소(분)"
                )
            )
            set(
                "avg", MenuDetail(
                    value = hearts.average().toFloat(),
                    img = R.drawable.ic_heart_solid,
                    intro = "평균(분)"
                )
            )
            set(
                "max", MenuDetail(
                    value = hearts.maxOrNull()?.toFloat() ?: 0f,
                    img = R.drawable.ic_heart_solid,
                    intro = "최대(분)"
                )
            )
        }
    }.onFailure { e ->
        Log.e("test","error: ${e.message}")
    }

    companion object {
        fun getInitValues() = HeartRateMenu(
            heartRates = listOf(
                HeartRate(
                    time = Instant.now(),
                    perMinutes = 0
                )
            )
        )
    }
}

internal data class HeartRate(
    val time: Instant,
    val perMinutes: Int
) {
    companion object {
        fun getInitValues() = HeartRate(
            time = Instant.now(),
            perMinutes = 0
        )
    }
}
