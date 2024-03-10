package jinproject.stepwalk.mission.screen.missiondetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.model.mission.CalorieMissionLeaf
import jinproject.stepwalk.domain.model.mission.MissionComposite
import jinproject.stepwalk.domain.model.mission.MissionList
import jinproject.stepwalk.domain.model.onException
import jinproject.stepwalk.domain.model.onSuccess
import jinproject.stepwalk.domain.usecase.mission.GetMissionList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class MissionDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle,
    private val getMissionList: GetMissionList
) : ViewModel() {
    var title = ""
    private val _missionList: MutableStateFlow<MissionList> = MutableStateFlow(
        MissionList(
            title = "",
            list = listOf(
                MissionComposite(
                    designation = "",
                    intro = "",
                    missions = listOf(
                        CalorieMissionLeaf(
                            achieved = 0,
                            goal = 10
                        )
                    )
                )
            )
        )
    )
    val missionList get() = _missionList.asStateFlow()

    init {
        title = savedStateHandle.get<String>("title") ?: ""
        fetchMission(title)
    }

    private fun fetchMission(title: String) {
        viewModelScope.launch(Dispatchers.IO) {
            getMissionList(title).onStart {

            }.onEach { missions ->
                missions.onSuccess { mission ->
                    _missionList.update { mission!! }
                }.onException { code, message ->

                }
            }.launchIn(viewModelScope)
        }
    }

}
