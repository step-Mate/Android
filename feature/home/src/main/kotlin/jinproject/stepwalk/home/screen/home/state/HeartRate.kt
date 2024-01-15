package jinproject.stepwalk.home.screen.home.state

import androidx.compose.runtime.Stable
import java.time.ZonedDateTime

@Stable
internal data class HeartRate(
    override val startTime: ZonedDateTime,
    override val endTime: ZonedDateTime,
    val avg: Long,
    val max: Long,
    val min: Long,
) : HealthCare(startTime, endTime, avg)

internal class HeartRateFactory : HealthCareFactory<HeartRate> {
    private var max: Long = 0L
    private var min: Long = 0L

    override fun create(
        startTime: ZonedDateTime,
        endTime: ZonedDateTime,
        figure: Long,
    ): HeartRate {
        return HeartRate(startTime, endTime, figure, max, min)
    }

    override fun create(
        startTime: ZonedDateTime,
        endTime: ZonedDateTime,
        extras: HealthCareExtras,
    ): HeartRate {
        max = extras[HealthCareExtras.KEY_HEART_RATE_MAX] ?: 0L
        min = extras[HealthCareExtras.KEY_HEART_RATE_MIN] ?: 0L
        val avg: Long = extras[HealthCareExtras.KEY_HEART_RATE_AVG] ?: 0L
        return create(startTime, endTime, avg)
    }

    companion object {
        private var minstance: HeartRateFactory? = null

        val instance: HeartRateFactory
            get() {
                if (minstance == null)
                    minstance = HeartRateFactory()

                return minstance!!
            }
    }
}

internal class HeartRateTabFactory(
    override var healthCareList: List<HeartRate>,
) : HealthTabFactory<HeartRate>(healthCareList) {

    override fun create(time: Time, goal: Int): HealthTab {
        return kotlin.runCatching {
            HealthTab(
                header = HealthPage(total, goal, title = "심박수"),
                graph = time.getGraph(healthCareList),
                menu = getMenuList()
            )
        }.getOrElse { e ->
            if (e is IllegalArgumentException) {
                getDefaultValues(time)
            } else
                throw e
        }
    }

    private fun getMenuList(): List<MenuItem> = listOf(
        HeartMaxMenuFactory.create(healthCareList),
        HeartAvgMenuFactory.create(healthCareList),
        HeartMinMenuFactory.create(healthCareList)
    )

    override fun getDefaultValues(time: Time): HealthTab =
        HealthTab(
            header = HealthPage(-1, 1, title = "심박수"),
            graph = HealthTab.getDefaultGraphItems(time.toNumberOfDays()),
            menu = kotlin.run {
                val now = ZonedDateTime.now()
                val defaultList = listOf(HeartRateFactory.instance.create(now, now, 0))
                listOf(
                    HeartMaxMenuFactory.create(defaultList),
                    HeartAvgMenuFactory.create(defaultList),
                    HeartMinMenuFactory.create(defaultList)
                )
            }
        )

    companion object {
        private var _instance: HeartRateTabFactory? = null

        fun getInstance(healthCareList: List<HeartRate>): HeartRateTabFactory {
            if (_instance == null)
                _instance = HeartRateTabFactory(healthCareList)
            else
                _instance!!.healthCareList = healthCareList

            return _instance!!
        }
    }
}