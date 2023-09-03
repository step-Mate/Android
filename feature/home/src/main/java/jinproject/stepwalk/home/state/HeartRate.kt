package jinproject.stepwalk.home.state

import android.util.Log
import jinproject.stepwalk.design.R
import java.time.Instant
import java.time.ZoneId
import java.util.SortedMap

internal data class HeartRateMenu(
    val heartRates: List<HeartRate>
): HealthMenu {
    override val details: MutableMap<String, MenuDetail> = getMenuDetails()
    override val graphItems: SortedMap<Int, Long> = toGraphItems()

    private fun toGraphItems() = kotlin.run {
        val items = mutableMapOf<Int, Long>().apply {
            repeat(24) { hour ->
                this[hour] = 0
            }
        }

        heartRates.forEach { heart ->
            val instant = heart.time
            val key = instant.atZone(ZoneId.of("Asia/Seoul")).hour
            items[key] = (items[key] ?: 0) + heart.perMinutes
        }

        items.toSortedMap(compareBy { it })
    }

    private fun getMenuDetails() = kotlin.runCatching {
        val details = mutableMapOf<String, MenuDetail>()
        val hearts = heartRates.map { it.perMinutes }

        details.apply {
            set(
                "min", MenuDetail(
                    value = hearts.min().toFloat(),
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
                    value = hearts.max().toFloat(),
                    img = R.drawable.ic_heart_solid,
                    intro = "최대(분)"
                )
            )
        }
    }.onFailure { e ->
        Log.e("test","error: ${e.printStackTrace()}")
    }.getOrElse {
        mutableMapOf<String, MenuDetail>()
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
