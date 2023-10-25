package jinproject.stepwalk.home.screen.state

import androidx.compose.runtime.Stable

@Stable
internal data class HeartRate(
    override val startTime: Long,
    override val endTime: Long,
    val avg: Long,
    val min: Long,
    val max: Long
) : HealthCare(startTime, endTime, avg)

internal class HeartRateFactory : HealthCareFactory<HeartRate> {
    private var max: Long = 0L
    private var min: Long = 0L

    override fun create(
        startTime: Long,
        endTime: Long,
        figure: Long,
    ): HeartRate {
        return HeartRate(startTime, endTime, figure, max, min)
    }

    override fun create(startTime: Long, endTime: Long, extras: HealthCareExtras): HeartRate {
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
        return HealthTab(
            header = HealthPage(total, goal, title = "심박수"),
            graph = time.getGraph(healthCareList),
            menu = if (healthCareList.isEmpty()) emptyList() else getMenuList()
        )
    }

    override fun getMenuList(): List<MenuItem> = listOf(
        HeartMaxMenuFactory.create(healthCareList),
        HeartAvgMenuFactory.create(healthCareList),
        HeartMinMenuFactory.create(healthCareList)
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