package jinproject.stepwalk.home.state

import androidx.compose.runtime.Stable
import jinproject.stepwalk.design.R
import jinproject.stepwalk.domain.METs
import java.time.Instant
import java.time.ZoneId
import java.util.SortedMap

@Stable
internal data class StepMenu(
    val steps: List<Step>
): HealthMenu {
    override val details: MutableMap<String, MenuDetail> = getMenuDetails(55f)
    override val graphItems: SortedMap<Int, Long> = toGraphItems()

    private fun toGraphItems() = kotlin.run {
        val items = mutableMapOf<Int, Long>().apply {
            repeat(24) { hour ->
                this[hour] = 0
            }
        }

        steps.forEach { step ->
            val instant = Instant.ofEpochSecond(step.end.toLong() * 60L)
            val key = instant.atZone(ZoneId.of("Asia/Seoul")).hour
            items[key] = (items[key] ?: 0) + step.distance
        }

        items.toSortedMap(compareBy { it })
    }

    private fun getMenuDetails(kg: Float) = kotlin.run {
        val details = mutableMapOf<String, MenuDetail>()
        val type = steps.first().type
        val minutes = steps.map { it.end - it.start }.reduce { acc, i -> acc + i }
        val steps = steps.map { it.distance }.reduce { acc, l -> acc + l }

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
    companion object {
        fun getInitValues() = StepMenu(
            steps = listOf(
                Step(
                    distance = 0,
                    start = 0,
                    end = 0,
                    type = METs.Walk
                )
            )
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