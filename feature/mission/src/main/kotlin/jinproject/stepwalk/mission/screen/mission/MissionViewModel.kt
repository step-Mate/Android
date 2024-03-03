package jinproject.stepwalk.mission.screen.mission

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.core.catchDataFlow
import jinproject.stepwalk.domain.model.mission.MissionList
import jinproject.stepwalk.domain.usecase.auth.CheckHasTokenUseCase
import jinproject.stepwalk.domain.usecase.mission.GetAllMissionList
import jinproject.stepwalk.domain.usecase.mission.UpdateMissionList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class MissionViewModel @Inject constructor(
    getAllMissionList: GetAllMissionList,
    checkHasTokenUseCase: CheckHasTokenUseCase,
    private val updateMissionList: UpdateMissionList,
) : ViewModel() {
    private val _uiState: MutableSharedFlow<UiState> = MutableSharedFlow(replay = 1)
    val uiState: SharedFlow<UiState> get() = _uiState.asSharedFlow()

    private val _missionList: MutableStateFlow<List<MissionList>> = MutableStateFlow(emptyList())
    val missionList get() = _missionList.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            updateMissionList()
        }
        checkHasTokenUseCase().flatMapLatest { token ->
            if (token) {
                getAllMissionList().onEach { missions ->
                    _missionList.update { missions }
                    _uiState.emit(UiState.Success)
                }
            } else {
                flow {
                    _uiState.emit(UiState.Error(CANNOT_LOGIN_EXCEPTION))
                }
            }
        }.catchDataFlow(
            action = { e ->
                if (e.code == 402)
                    CANNOT_LOGIN_EXCEPTION
                else
                    e
            },
            onException = { e ->
                _uiState.emit(UiState.Error(e))
            }
        ).launchIn(viewModelScope)
    }


    @Stable
    sealed class UiState {
        data object Loading : UiState()
        data object Success : UiState()
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }

    companion object {
        val CANNOT_LOGIN_EXCEPTION = IllegalStateException("로그인 불가")
    }
}
