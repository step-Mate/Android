package jinproject.stepwalk.mission.screen.state

import androidx.compose.runtime.Stable
import androidx.compose.ui.graphics.Color
import jinproject.stepwalk.design.theme.StepWalkColor

@Stable
data class Mission(
    val title : String = "",
    val missionValue: MissionValue = MissionValue(1,8),
    val contentColor : Color = StepWalkColor.blue_200.color,
    val containerColor : Color = StepWalkColor.blue_400.color
)

@Stable
data class MissionValue(
    val nowValue : Int,
    val maxValue : Int
)


object MissionList {
    val list = listOf(
        Mission(
            title = "뉴비 미션",
            missionValue = MissionValue(3,7)
        ),
        Mission(
            title = "일일 미션",
            missionValue = MissionValue(1,9),
            contentColor = StepWalkColor.yellow_200.color,
            containerColor = StepWalkColor.yellow_400.color
        ),
        Mission(
            title = "주간 미션",
            missionValue = MissionValue(3,9)
        ),
        Mission(
            title = "타임 미션",
            missionValue = MissionValue(8,9)
        ),
        Mission(
            title = "도전 미션",
            missionValue = MissionValue(5,9)
        ),
        Mission(
            title = "런닝 미션",
        )
    )
}
