package jinproject.stepwalk.ranking.detail

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.usecase.GetUserDetailUseCase
import jinproject.stepwalk.ranking.rank.User
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


    //TODO rankingScreen 에서 받아온 userName 으로 세부 정보 받아오는 Api 연결
    private fun getUserDetailInfo(userName: String, maxStep: Int?) =
        getUserDetailUseCase(userName).onEach { user ->
            _uiState.update {
                UiState.Success(
                    user.asUser(
                        maxStep ?: user.stepRank.getTotalHealthFigure()
                    )
                )
            }
        }.launchIn(viewModelScope)

    @Stable
    sealed class UiState {
        data object Loading : UiState()
        data class Success(val user: User) : UiState()
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }
}
