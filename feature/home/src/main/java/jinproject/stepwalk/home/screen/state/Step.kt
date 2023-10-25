package jinproject.stepwalk.home.screen.state

import androidx.compose.runtime.Stable
import jinproject.stepwalk.domain.model.METs

@Stable
internal data class Step(
    val mets: METs,
    override val startTime: Long,
    override val endTime: Long,
    val distance: Long
) : HealthCare(startTime, endTime, distance)

internal class StepFactory : HealthCareFactory<Step> {
    private var mets: METs? = null

    fun create(
        startTime: Long,
        endTime: Long,
        figure: Long,
        mets: METs
    ) {
        this.mets = mets
        create(startTime, endTime, figure)
    }

    private fun getDefaultMETs(): METs {
        if (mets == null)
            this.mets = METs.Walk

        return this.mets!!
    }

    override fun create(
        startTime: Long,
        endTime: Long,
        figure: Long,
    ): Step {
        return Step(mets ?: getDefaultMETs(), startTime, endTime, figure)
    }

    override fun create(startTime: Long, endTime: Long, extras: HealthCareExtras): Step {
        val step: Long = extras[HealthCareExtras.KEY_STEP] ?: 0L
        return create(startTime, endTime, step)
    }

    companion object {
        private var minstance: StepFactory? = null

        val instance: StepFactory
            get() {
                if (minstance == null)
                    minstance = StepFactory()

                return minstance!!
            }
    }
}

internal class StepTabFactory(
    override var healthCareList: List<Step>,
) : HealthTabFactory<Step>(healthCareList) {

    override fun create(time: Time, goal: Int): HealthTab {
        return HealthTab(
            header = HealthPage(total, goal, title = "걸음수"),
            graph = time.getGraph(healthCareList),
            menu = getMenuList()
        )
    }

    override fun getMenuList(): List<MenuItem> = listOf(
        DistanceMenuFactory.create(total),
        TimeMenuFactory.create(total),
        CaloriesMenuFactory.create(total)
    )

    companion object {
        private var _instance: StepTabFactory? = null

        fun getInstance(healthCareList: List<Step>): StepTabFactory {
            if (_instance == null)
                _instance = StepTabFactory(healthCareList)
            else
                _instance!!.healthCareList = healthCareList

            return _instance!!
        }
    }
}

internal fun List<Step>.total() = this.map { it.distance }.fold(0L) { acc, l -> acc + l }