package jinproject.stepwalk.ranking.detail

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.core.catchDataFlow
import jinproject.stepwalk.domain.model.mission.MissionComponent
import jinproject.stepwalk.domain.usecase.user.GetUserDetailUseCase
import jinproject.stepwalk.domain.usecase.user.ManageFriendUseCase
import jinproject.stepwalk.ranking.rank.Rank
import jinproject.stepwalk.ranking.rank.RankingViewModel
import jinproject.stepwalk.ranking.rank.state.asUser
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class UserDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserDetailUseCase: GetUserDetailUseCase,
    private val manageFriendUseCase: ManageFriendUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> get() = _uiState.asStateFlow()

    private val userName = savedStateHandle.get<String>("userName")
    private val maxStep = savedStateHandle.get<Int>("maxStep")
    val isFriend = savedStateHandle.get<Boolean>("isFriend") ?: false

    private val _isFriendState: MutableStateFlow<Boolean> = MutableStateFlow(isFriend)
    val isFriendState: StateFlow<Boolean> get() = _isFriendState.asStateFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { coroutineContext, t ->
        viewModelScope.launch {
            _snackBarState.emit(
                SnackBarMessage(
                    headerMessage = "일시적인 장애가 발생했어요.",
                    contentMessage = t.message.toString()
                )
            )
        }
    }

    private val _snackBarState: MutableSharedFlow<SnackBarMessage> = MutableSharedFlow(replay = 0)
    val snackBarState: SharedFlow<SnackBarMessage> get() = _snackBarState.asSharedFlow()

    init {
        if (!userName.isNullOrBlank())
            getUserDetailInfo(userName, maxStep)
    }

    private fun getUserDetailInfo(userName: String, maxStep: Int?) = getUserDetailUseCase(userName)
        .onEach { user ->
            _uiState.update {
                UiState.Success(
                    user.asUser(
                        maxStep ?: user.stepRank.getTotalHealthFigure()
                    )
                )
            }
        }.catchDataFlow(
            action = { e ->
                when (e.code) {
                    402 -> RankingViewModel.CANNOT_LOGIN_EXCEPTION
                    404 -> IllegalArgumentException("[$userName] 과 일치하는 사용자가 없어요.")
                    else -> e
                }
            },
            onException = { t ->
                _uiState.update { UiState.Error(t) }
            }
        ).launchIn(viewModelScope)

    fun manageFriendShip() {
        if (!isFriendState.value) {
            userName?.let { name ->
                viewModelScope.launch(coroutineExceptionHandler) {
                    manageFriendUseCase.addFriend(name)

                    _snackBarState.emit(
                        SnackBarMessage(
                            headerMessage = "$userName 님에게 친구 신청을 했어요.",
                            contentMessage = "$userName 님이 수락하실 때 까지 조금만 기다려주세요."
                        )
                    )
                }
            }
        } else {
            userName?.let { name ->
                viewModelScope.launch(coroutineExceptionHandler) {
                    manageFriendUseCase.deleteFriend(name)

                    _isFriendState.update { false }

                    _snackBarState.emit(
                        SnackBarMessage(
                            headerMessage = "$userName 님을 목록에서 삭제했어요.",
                            contentMessage = "$userName 님에게도 더이상 친구로 표시되지 않아요."
                        )
                    )
                }
            }
        }
    }

    @Stable
    sealed class UiState {
        data object Loading : UiState()
        data class Success(val user: User) : UiState()
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }
}

@Stable
data class User(
    val info: Rank,
    val steps: List<Long>,
    val maxStep: Int,
    val latestMissions: List<MissionComponent>,
) {
    companion object {
        fun getInitValues() = User(
            info = Rank.getInitValues(),
            steps = emptyList(),
            maxStep = 0,
            latestMissions = emptyList()
        )
    }
}
