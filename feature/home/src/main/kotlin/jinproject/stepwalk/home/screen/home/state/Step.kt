package jinproject.stepwalk.home.screen.home.state

import androidx.compose.runtime.Stable
import java.time.ZonedDateTime

@Stable
internal data class Step(
    override val startTime: ZonedDateTime,
    override val endTime: ZonedDateTime,
    val distance: Long,
) : HealthCare(startTime, endTime, distance)

internal class StepFactory : HealthCareFactory<Step> {

    override fun create(
        startTime: ZonedDateTime,
        endTime: ZonedDateTime,
        figure: Long,
    ): Step {
        return Step(startTime, endTime, figure)
    }

    override fun create(
        startTime: ZonedDateTime,
        endTime: ZonedDateTime,
        extras: HealthCareExtras,
    ): Step {
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
    val user: User,
) : HealthTabFactory<Step>(healthCareList) {

    override fun create(time: Time, goal: Int): HealthTab {
        return kotlin.runCatching {
            HealthTab(
                header = HealthPage(total, goal, title = "걸음수"),
                graph = time.getGraph(healthCareList),
                menu = getMenuList(
                    figure = total,
                    weight = user.weight,
                )
            )
        }.getOrElse { e ->
            if (e is IllegalArgumentException) {
                getDefaultValues(time)
            } else
                throw e
        }
    }

    override fun getDefaultValues(time: Time): HealthTab =
        HealthTab(
            header = HealthPage(-1, 1, title = "걸음수"),
            graph = HealthTab.getDefaultGraphItems(time.toNumberOfDays()),
            menu = run {
                val user = User.getInitValues()

                listOf(
                    DistanceMenuFactory.create(0),
                    TimeMenuFactory.create(0),
                    CaloriesMenuFactory(
                        weight = user.weight,
                    ).create(0)
                )
            }
        )

    companion object {
        private var _instance: StepTabFactory? = null

        fun getInstance(healthCareList: List<Step>, user: User): StepTabFactory {
            if (_instance == null)
                _instance = StepTabFactory(healthCareList, user)
            else
                _instance!!.healthCareList = healthCareList

            return _instance!!
        }

        fun getMenuList(figure: Long, weight: Int): List<MenuItem> = listOf(
            DistanceMenuFactory.create(figure),
            TimeMenuFactory.create(figure),
            CaloriesMenuFactory(
                weight = weight,
            ).create(figure)
        )
    }
}