package jinproject.stepwalk.home.state

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import java.time.Instant

internal interface HealthMenu {
    @Stable
    val details: Map<String, MenuDetail>

    @Stable
    val graphItems: List<Long>
}

@Stable
internal data class MenuDetail(
    val value: Float,
    @DrawableRes val img: Int,
    val intro: String
)

@Stable
internal interface GraphItem {
    val startTime: Long
    val endTime: Long
    val graphValue: Long
}

internal fun <T: GraphItem> List<T>.addGraphItems(time: Time, dataList: ArrayList<Long>) {
    this.forEach { item ->
        val startTime = item.startTime
        val value = item.graphValue

        val instant = Instant.ofEpochSecond(startTime)
        val key = time.toZonedOffset(instant)

        when (time) {
            Time.Day -> dataList[key] = value
            else -> dataList[key - 1] = value
        }
    }
}