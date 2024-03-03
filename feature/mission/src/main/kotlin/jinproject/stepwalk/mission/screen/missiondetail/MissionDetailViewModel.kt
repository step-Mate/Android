package jinproject.stepwalk.mission.screen.missiondetail

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.core.catchDataFlow
import jinproject.stepwalk.domain.model.mission.MissionList
import jinproject.stepwalk.domain.usecase.auth.CheckHasTokenUseCase
import jinproject.stepwalk.domain.usecase.mission.GetMissionList
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
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class MissionDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getMissionList: GetMissionList,
    private val checkHasTokenUseCase: CheckHasTokenUseCase,
) : ViewModel() {
    private val _uiState: MutableSharedFlow<UiState> = MutableSharedFlow(replay = 1)
    val uiState: SharedFlow<UiState> get() = _uiState.asSharedFlow()

    val title: String = savedStateHandle.get<String>("title") ?: ""

    private val _missionList: MutableStateFlow<MissionList> = MutableStateFlow(
        MissionList(
            title = "",
            list = listOf()
        )
    )
    val missionList get() = _missionList.asStateFlow()

    init {
        fetchMission(title)
    }

    private fun fetchMission(title: String) =
        checkHasTokenUseCase().flatMapLatest { token ->
            if (token) {
                getMissionList(title).onEach { mission ->
                    _missionList.update { mission }
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
