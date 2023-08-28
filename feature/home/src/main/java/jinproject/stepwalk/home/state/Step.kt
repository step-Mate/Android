package jinproject.stepwalk.home.state

import androidx.compose.runtime.Stable
import jinproject.stepwalk.design.R
import jinproject.stepwalk.domain.METs
import jinproject.stepwalk.home.HealthMenu
import jinproject.stepwalk.home.MenuDetail
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalField

@Stable
data class Step(
    val distance: Long,
    val start: Int,
    val end: Int,
    val type: METs
) : HealthMenu {
    override val details: MutableMap<String, MenuDetail> = mutableMapOf()

    companion object {
        fun getInitValues() = Step(
            distance = 0L,
            start = 0,
            end = 0,
            type = METs.Walk
        )
    }
}

fun List<Step>.toGraphItems() = kotlin.run {
    val items = mutableMapOf<Int, Long>()

    this.forEach { step ->
        val instant = Instant.ofEpochSecond(step.end.toLong() * 60L)
        val key = instant.atZone(ZoneId.of("Asia/Seoul")).hour
        when(items[key] == null) {
            true -> items[key] = step.distance
            false -> items[key] = items[key]!! + step.distance
        }
    }

    items
}

fun List<Step>.getMenuDetails(kg: Float) = kotlin.run {
    val details = mutableMapOf<String, MenuDetail>()
    val type = this.first().type
    val minutes = this.map { it.end - it.start }.reduce { acc, i -> acc + i }
    val steps = this.map { it.distance }.reduce { acc, l -> acc + l }

    details.apply {
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
}