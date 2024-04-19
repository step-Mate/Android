package com.stepmate.profile.screen.profile

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.stepmate.core.catchDataFlow
import com.stepmate.core.getCharacter
import com.stepmate.core.isValidPassword
import com.stepmate.domain.model.BodyData
import com.stepmate.domain.model.user.User
import com.stepmate.domain.usecase.auth.CheckHasTokenUseCase
import com.stepmate.domain.usecase.auth.LogoutUseCases
import com.stepmate.domain.usecase.user.GetBodyDataUseCases
import com.stepmate.domain.usecase.user.GetMyInfoUseCases
import com.stepmate.domain.usecase.user.WithdrawAccountUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed interface ProfileEvent {
    data object Logout : ProfileEvent
    data object Secession : ProfileEvent
    data class Password(val value: String) : ProfileEvent
    data class MoveToEdit(val navigate: (String, String, Boolean) -> Unit, val anonymous: Boolean) :
        ProfileEvent
}

enum class PasswordValid {
    Blank, NotValid, Valid, NotMatch, Success
}

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getMyInfoUseCases: GetMyInfoUseCases,
    private val logoutUseCases: LogoutUseCases,
    checkHasTokenUseCase: CheckHasTokenUseCase,
    private val withdrawAccountUseCases: WithdrawAccountUseCases,
    getBodyDataUseCases: GetBodyDataUseCases
) : ViewModel() {
    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> get() = _uiState.asStateFlow()

    private val _user = MutableStateFlow(User.getInitValues().copy(name = "",designation = "회원가입시 다양한 기능을 사용할 수 있어요"))
    val user get() = _user.asStateFlow()

    private val _bodyData = MutableStateFlow(BodyData())
    val bodyData get() = _bodyData.asStateFlow()

    private val _passwordValid = MutableStateFlow(PasswordValid.Blank)
    val passwordValid get() = _passwordValid.asStateFlow()
    private var password = ""


    init {
        checkHasTokenUseCase().flatMapLatest { token ->
            if (token) {
                getMyInfoUseCases().onEach { user ->
                    _user.update { user.copy(character = getCharacter(user.level)) }
                    _uiState.emit(UiState.Login)
                }
            } else {
                flow {
                    _user.update { User.getInitValues().copy(name = "",designation = "회원가입시 다양한 기능을 사용할 수 있어요") }
                    _uiState.emit(UiState.Anonymous)
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

        getBodyDataUseCases().onEach { bodyData ->
            _bodyData.update { bodyData }
        }.launchIn(viewModelScope)
    }


    fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.Logout -> {
                viewModelScope.launch(Dispatchers.IO) {
                    logoutUseCases()
                }
            }

            ProfileEvent.Secession -> {
                withdrawAccountUseCases(password).onEach {
                    if (it) {
                        _passwordValid.update { PasswordValid.Success }
                        logoutUseCases()
                    }
                }.catchDataFlow(
                    action = { e ->
                        if (e.code == 402)
                            CANNOT_LOGIN_EXCEPTION
                        else
                            e
                    },
                    onException = { e ->
                        _passwordValid.update { PasswordValid.NotMatch }
                    }
                ).launchIn(viewModelScope)
            }

            is ProfileEvent.Password -> {
                if (event.value.isValidPassword() && event.value.isNotEmpty()) {
                    _passwordValid.update { PasswordValid.Valid }
                    password = event.value
                } else {
                    _passwordValid.update { PasswordValid.NotValid }
                }
            }

            is ProfileEvent.MoveToEdit -> {
                viewModelScope.launch {
                    event.navigate(user.value.name, user.value.designation, event.anonymous)
                }
            }
        }
    }

    @Stable
    sealed class UiState {
        data object Loading : UiState()
        data object Login : UiState()
        data object Anonymous : UiState()
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }

    companion object {
        val CANNOT_LOGIN_EXCEPTION = IllegalStateException("로그인 불가")
    }
}