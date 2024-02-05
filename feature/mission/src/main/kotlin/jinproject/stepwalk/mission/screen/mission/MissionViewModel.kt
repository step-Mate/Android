package jinproject.stepwalk.mission.screen.mission

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.model.MissionList
import jinproject.stepwalk.domain.model.MissionMode
import jinproject.stepwalk.domain.model.StepMission
import jinproject.stepwalk.design.R
import javax.inject.Inject

@HiltViewModel
internal class MissionViewModel @Inject constructor(

) : ViewModel(){

    val missionList = mutableStateListOf<MissionList>()

    init {
        missionList.addAll(TestList.list)
    }
}


//임시 테스트용
internal object TestList {
    val list = listOf(
        MissionList(
            title = "일일 미션",
            icon = R.drawable.ic_fire,
            mode = MissionMode.time,
            list = listOf(
                StepMission(
                    designation = "100걸음",
                    intro = "100걸음을 달성",
                    achieved = 10,
                    goal = 100
                ),
                StepMission(
                    designation = "100 심박수",
                    intro = "",
                    achieved = 10,
                    goal = 100
                ),
                StepMission(
                    designation = "300",
                    intro = "",
                    achieved = 10,
                    goal = 100
                ),
                StepMission(
                    designation = "400",
                    intro = "",
                    achieved = 10,
                    goal = 100
                ),
            )
        ),
        MissionList(
            title = "걸음수 미션",
            icon = R.drawable.ic_heart_solid,
            mode = MissionMode.repeat,
            list = listOf(
                StepMission(
                    designation = "100걸음",
                    intro = "100걸음을 달성",
                    achieved = 10,
                    goal = 100
                ),
                StepMission(
                    designation = "200",
                    intro = "",
                    achieved = 10,
                    goal = 100
                ),
                StepMission(
                    designation = "300",
                    intro = "",
                    achieved = 10,
                    goal = 100
                ),
                StepMission(
                    designation = "400",
                    intro = "",
                    achieved = 10,
                    goal = 100
                ),
            )
        )
    )
}