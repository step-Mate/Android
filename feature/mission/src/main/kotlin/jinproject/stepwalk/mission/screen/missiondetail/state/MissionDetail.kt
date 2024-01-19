package jinproject.stepwalk.mission.screen.missiondetail.state

import androidx.compose.runtime.Stable
import jinproject.stepwalk.mission.screen.mission.state.MissionValue

@Stable
internal data class MissionDetail(
    val title: String = "",
    val value: MissionValue = MissionValue()
)