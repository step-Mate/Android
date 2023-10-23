package jinproject.stepwalk.home.state

import androidx.compose.runtime.Stable
import jinproject.stepwalk.design.R
import jinproject.stepwalk.domain.model.METs

@Stable
internal class StepMenu(
    val steps: List<Step>,
    override var graphItems: List<Long>,
) : HealthMenu {
    override val details: Map<String, MenuDetail> = getMenuDetails()

    private fun getMenuDetails(kg: Float = .0f) = kotlin.run {
        val type = steps.firstOrNull()?.mets ?: METs.Walk
        val minutes = steps.sumOf { it.endTime - it.startTime }
        val steps = steps.total()

        mutableMapOf<String, MenuDetail>().apply {
            set(
                "calories", MenuDetail(
                    value = (steps * 3).toFloat() / 1000,
                    img = R.drawable.ic_fire,
                    intro = "칼로리(kg)"
                )
            )
            set(
                "minutes", MenuDetail(
                    value = (steps.toFloat() * 0.0008).toFloat() * 15,
                    img = R.drawable.ic_time,
                    intro = "시간(분)"
                )
            )
            set(
                "distance", MenuDetail(
                    value = (steps.toFloat() * 0.0008).toFloat(),
                    img = R.drawable.ic_person_walking,
                    intro = "거리(km)"
                )
            )
        }
    }

    companion object {
        fun getInitValues() = kotlin.run {
            val steps = listOf(
                Step(
                    distance = 0,
                    startTime = 0,
                    endTime = 0,
                    mets = METs.Walk
                )
            )
            StepMenu(
                steps = steps,
                graphItems = Time.Day.getGraphItems { time, items ->
                    steps.addGraphItems(
                        time,
                        items
                    )
                }
            )
        }
    }
}

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

    fun getDefaultMETs(): METs {
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
}

internal fun List<Step>.total() = this.map { it.distance }.fold(0L) { acc, l -> acc + l }