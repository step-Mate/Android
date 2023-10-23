package jinproject.stepwalk.home.state

import androidx.compose.runtime.Stable
import jinproject.stepwalk.design.R
import java.time.Instant

internal class HeartRateMenu(
    val heartRates: List<HeartRate>,
    override var graphItems: List<Long>
) : HealthMenu {
    override val details: Map<String, MenuDetail> = getMenuDetails()

    private fun getMenuDetails() = mutableMapOf<String, MenuDetail>().apply {
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

    companion object {
        fun getInitValues() = kotlin.run {
            val heartRates = listOf(
                HeartRate(
                    startTime = Instant.now().epochSecond,
                    endTime = Instant.now().epochSecond,
                    min = 0,
                    max = 0,
                    avg = 0
                )
            )

            HeartRateMenu(
                heartRates = heartRates,
                graphItems = Time.Day.getGraphItems { time, items ->
                    heartRates.addGraphItems(
                        time,
                        items
                    )
                }
            )
        }
    }
}

@Stable
internal data class HeartRate(
    override val startTime: Long,
    override val endTime: Long,
    val avg: Long,
    val min: Long,
    val max: Long
) : HealthCare(startTime, endTime, avg)

internal class HeartRateFactory(
    private val max: Long,
    private val min: Long
) : HealthCareFactory<HeartRate> {

    override fun create(
        startTime: Long,
        endTime: Long,
        figure: Long,
    ): HeartRate {
        return HeartRate(startTime, endTime, figure, max, min)
    }
}
