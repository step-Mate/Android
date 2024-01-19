package jinproject.stepwalk.mission.screen.missonrepeat

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.mission.screen.state.MissionDetail
import javax.inject.Inject

@HiltViewModel
internal class MissionRepeatViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel(){
    private var title = ""
    val list = listOf(MissionDetail("1000000"),MissionDetail("1000000"))
    var type = ""
    init {
        title = savedStateHandle.get<String>("title") ?: ""
        type = title.split(" ").first()
    }

}

