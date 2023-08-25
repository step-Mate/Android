package jinproject.stepwalk.app.ui.home.state

import androidx.compose.runtime.Stable
import jinproject.stepwalk.app.ui.home.HealthMenu
import jinproject.stepwalk.app.ui.home.MenuDetail
import jinproject.stepwalk.design.R
import jinproject.stepwalk.domain.METs

@Stable
data class Step(
    val distance: Long,
    val minutes: Int,
    val type: METs
) : HealthMenu {
    override val details: MutableMap<String, MenuDetail> = mutableMapOf()

    fun setMenuDetails(kg: Float) = with(details) {
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
                value = (distance.toFloat() * 0.0008).toFloat(),
                img = R.drawable.ic_person_walking,
                intro = "거리(km)"
            )
        )
    }


    companion object {
        fun getInitValues() = Step(
            distance = 0L,
            minutes = 0,
            type = METs.Walk
        )
    }

}