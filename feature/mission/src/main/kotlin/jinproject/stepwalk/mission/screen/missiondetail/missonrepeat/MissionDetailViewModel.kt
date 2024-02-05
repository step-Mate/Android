package jinproject.stepwalk.mission.screen.missiondetail.missonrepeat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.design.R
import jinproject.stepwalk.domain.model.MissionList
import jinproject.stepwalk.domain.model.MissionMode
import jinproject.stepwalk.domain.model.StepMission

import javax.inject.Inject

@HiltViewModel
internal class MissionDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel(){
    var title = ""
    var mode = MissionMode.repeat
    val list = MissionList(
        title = "일일 미션",
        icon = R.drawable.ic_fire,
        mode = mode,
        list = listOf(
            StepMission(
                designation = "100 심박수",
                intro = "100걸음을 달성",
                achieved = 10,
                goal = 100
            ),
            StepMission(
                designation = "200",
                intro = "",
                achieved = 0,
                goal = 100
            ),
            StepMission(
                designation = "300",
                intro = "",
                achieved = 0,
                goal = 100
            ),
            StepMission(
                designation = "400",
                intro = "",
                achieved = 0,
                goal = 100
            ),
        )
    )
    init {
        title = savedStateHandle.get<String>("title") ?: ""
        mode = savedStateHandle.get<Int>("mode")?.toMissionMode() ?: MissionMode.repeat
    }

}

fun Int.toMissionMode() = when{
    this == MissionMode.repeat.ordinal -> MissionMode.repeat
    this == MissionMode.time.ordinal -> MissionMode.time
    else -> MissionMode.repeat
}