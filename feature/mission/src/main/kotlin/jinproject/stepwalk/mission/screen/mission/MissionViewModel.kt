package jinproject.stepwalk.mission.screen.mission

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.mission.screen.state.Mission
import jinproject.stepwalk.mission.screen.state.MissionList
import jinproject.stepwalk.mission.screen.state.MissionValue
import jinproject.stepwalk.mission.screen.state.mergerToMission
import javax.inject.Inject

@HiltViewModel
internal class MissionViewModel @Inject constructor(

) : ViewModel(){

    val missionList = mutableStateListOf<Mission>()

    init {
        missionList.addAll(mergerToMission(MissionList.list, TestList.list))
    }




}

//임시 테스트용
internal object TestList {
    val list = listOf(
        MissionValue(),
        MissionValue(),
        MissionValue(),
        MissionValue(),
    )
}