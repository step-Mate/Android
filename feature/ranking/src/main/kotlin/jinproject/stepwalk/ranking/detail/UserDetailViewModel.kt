package jinproject.stepwalk.ranking.detail

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.core.catchDataFlow
import jinproject.stepwalk.domain.model.mission.MissionComponent
import jinproject.stepwalk.domain.usecase.user.GetUserDetailUseCase
import jinproject.stepwalk.ranking.rank.Rank
import jinproject.stepwalk.ranking.rank.RankingViewModel
import jinproject.stepwalk.ranking.rank.state.asUser
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class UserDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val getUserDetailUseCase: GetUserDetailUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> get() = _uiState.asStateFlow()

    init {
        val userName = savedStateHandle.get<String>("userName")
        val maxStep = savedStateHandle.get<Int>("maxStep")
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
