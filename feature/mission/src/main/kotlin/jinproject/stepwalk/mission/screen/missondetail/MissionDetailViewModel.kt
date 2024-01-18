package jinproject.stepwalk.mission.screen.missondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.mission.screen.state.MissionDetail
import javax.inject.Inject

@HiltViewModel
internal class MissionDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel(){
    var title = ""
    var mode = MissionMode.repeat
    val list = listOf(MissionDetail("1000000"),MissionDetail("1000000"))

    init {
        title = savedStateHandle.get<String>("title") ?: ""

    }

}

enum class MissionMode {
    time,repeat
}