package jinproject.stepwalk.mission.screen.missiontime

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.mission.screen.state.MissionDetail
import javax.inject.Inject

@HiltViewModel
internal class MissionTimeViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel(){
    var title = ""
    val list = listOf(MissionDetail("1000000"), MissionDetail("1000000"))

    init {
        title = savedStateHandle.get<String>("title") ?: ""
    }

}
