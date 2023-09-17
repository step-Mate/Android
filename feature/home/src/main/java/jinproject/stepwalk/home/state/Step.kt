package jinproject.stepwalk.home.state

import android.util.Log
import androidx.compose.runtime.Stable
import jinproject.stepwalk.design.R
import jinproject.stepwalk.domain.model.METs
import java.time.Instant
import java.util.SortedMap

@Stable
internal data class StepMenu(
    val steps: List<Step>,
): HealthMenu {
    override var details: Map<String, MenuDetail>? = null
    override var graphItems: List<Long>? = null

    fun setGraphItems(time: Time) = kotlin.run {
        val items = ArrayList<Long>(time.toRepeatTimes()).apply {
            repeat(time.toRepeatTimes()) { index ->
                add(index, 0L)
            }
        }

        steps.forEach { step ->
            val instant = Instant.ofEpochSecond(step.start)
            val key = time.toZonedOffset(instant)
            when (time) {
                Time.Day -> items[key] = step.distance
                else -> items[key - 1] = step.distance
            }
        }

        graphItems = when(time) {
            Time.Week -> items.sortDayOfWeek()
            else -> items
        }
    }

    fun setMenuDetails(kg: Float) = kotlin.runCatching {
        val type = steps.firstOrNull()?.type ?: METs.Walk
        val minutes = steps.sumOf { it.end - it.start }
        val steps = steps.total()

        details = mutableMapOf<String, MenuDetail>().apply {
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
    }.onFailure { e ->
        Log.e("test","error: ${e.printStackTrace()}")
    }

    companion object {
        fun getInitValues() = StepMenu(
            steps = listOf(
                Step(
                    distance = 0,
                    start = 0,
                    end = 0,
                    type = METs.Walk
                )
            ),
        ).apply {
            setGraphItems(Time.Day)
            setMenuDetails(55f)
        }
    }
}

@Stable
data class Step(
    val distance: Long,
    val start: Long,
    val end: Long,
    val type: METs
) {

    companion object {
        fun getInitValues() = Step(
            distance = 0L,
            start = 0,
            end = 0,
            type = METs.Walk
        )
    }
}

internal fun List<Step>.total() = this.map { it.distance }.fold(0L) { acc, l -> acc + l }