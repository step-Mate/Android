package jinproject.stepwalk.mission.screen.state

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.R

@Stable
data class Mission(
    val title: MissionTitle,
    val value: MissionValue
)

@Stable
data class MissionDetail(
    val title: String = "",
    val value: MissionValue = MissionValue()
)

@Stable
data class MissionTitle(
    val title : String = "",
    @DrawableRes val image : Int,
    val contentColor : Color = StepWalkColor.blue_200.color,
    val containerColor : Color = StepWalkColor.blue_400.color
)

@Stable
data class MissionValue(
    val now : Int = 0,
    val max : Int = 3
){
    fun progress() = now / max.toFloat()
    fun isMatched() = now == max
}

fun mergerToMission(title: List<MissionTitle>, value: List<MissionValue>) : List<Mission> = title.zip(value){ t,v ->
    Mission(t,v)
}

object MissionList {
    val list = listOf(
        MissionTitle(
            title = "주간 미션",
            image = R.drawable.ic_fire,
        ),
        MissionTitle(
            title = "월간 미션",
            image = R.drawable.ic_fire,
        ),
        MissionTitle(
            title = "걸음 미션",
            image = R.drawable.ic_fire,
        ),
        MissionTitle(
            title = "칼로리 미션",
            image = R.drawable.ic_fire,
        )
    )
}
