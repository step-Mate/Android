package jinproject.stepwalk.mission.screen.mission

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.model.mission.MissionList
import jinproject.stepwalk.domain.model.onException
import jinproject.stepwalk.domain.model.onSuccess
import jinproject.stepwalk.domain.usecase.mission.GetAllMissionList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class MissionViewModel @Inject constructor(
    private val getAllMissionList: GetAllMissionList
) : ViewModel() {
    private val _uiState: MutableSharedFlow<UiState> = MutableSharedFlow(replay = 1)
    val uiState: SharedFlow<UiState> get() = _uiState.asSharedFlow()

    private val _missionList: MutableStateFlow<List<MissionList>> = MutableStateFlow(emptyList())
    val missionList get() = _missionList.asStateFlow()

    init {
        fetchMissionList()
    }

    private fun fetchMissionList() {
        viewModelScope.launch(Dispatchers.IO) {
            getAllMissionList().onStart {
                _uiState.emit(UiState.Loading)
            }.onEach { missions ->
                missions.onSuccess { missionList ->
                    _missionList.update { missionList!! }
                    _uiState.emit(UiState.Success)
                }.onException { code, message ->
                    _uiState.emit(UiState.Error(Throwable(message)))
                }
            }.catch { e ->
                _uiState.emit(UiState.Error(e))
            }.launchIn(viewModelScope)
        }
    }


    @Stable
    sealed class UiState {
        data object Loading : UiState()
        data object Success : UiState()
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }
}
