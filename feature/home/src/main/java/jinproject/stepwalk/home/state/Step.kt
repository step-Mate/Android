package jinproject.stepwalk.home.state

import android.util.Log
import androidx.compose.runtime.Stable
import jinproject.stepwalk.design.R
import jinproject.stepwalk.domain.METs
import java.time.Instant
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.ChronoField
import java.time.temporal.TemporalAdjusters
import java.time.temporal.TemporalAmount
import java.time.temporal.TemporalField
import java.time.temporal.TemporalUnit
import java.util.SortedMap
import kotlin.time.Duration.Companion.days
import kotlin.time.toJavaDuration

@Stable
internal data class StepMenu(
    val steps: List<Step>,
): HealthMenu {
    override var details: Map<String, MenuDetail>? = null
    override var graphItems: SortedMap<Int, Long>? = null

    fun setGraphItems(time: Time) = kotlin.run {
        val items = mutableMapOf<Int, Long>().apply {
            repeat(time.toRepeatTimes()) { repeatTime ->
                this[repeatTime] = 0
            }
        }

        steps.forEach { step ->
            val instant = Instant.ofEpochSecond(step.start.toLong() * 60L)
            val key = time.toZonedOffset(instant)
            items[key] = (items[key] ?: 0) + step.distance
        }

        graphItems = items.toSortedMap(compareBy { it })
    }

    fun setMenuDetails(kg: Float) = kotlin.runCatching {
        val type = steps.firstOrNull()?.type ?: METs.Walk
        val minutes = steps.map { it.end - it.start }.fold(0) { acc, i -> acc + i }
        val steps = steps.total()

        details = mutableMapOf<String, MenuDetail>().apply {
            set(
                "calories", MenuDetail(
                    value = type.getMetsWeight() * 3.5f * kg * minutes * 5f / 1000,
                    img = R.drawable.ic_fire,
                    intro = "칼로리(kg)"
                )
            )
            set(
                "minutes", MenuDetail(
                    value = minutes.toFloat(),
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
        )
    }
}

@Stable
internal data class Step(
    val distance: Long,
    val start: Int,
    val end: Int,
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