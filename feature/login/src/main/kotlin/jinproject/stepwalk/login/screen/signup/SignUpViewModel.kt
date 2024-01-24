package jinproject.stepwalk.login.screen.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.model.onException
import jinproject.stepwalk.domain.model.onSuccess
import jinproject.stepwalk.domain.usecase.auth.CheckIdUseCase
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.screen.state.AuthState
import jinproject.stepwalk.login.utils.isValidPassword
import jinproject.stepwalk.login.utils.passwordMatches
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface SignUpEvent{
    data object NextStep : SignUpEvent
    data class Id(val value : String) : SignUpEvent
    data class Password(val value : String) : SignUpEvent
    data class RepeatPassword(val value : String) : SignUpEvent
}

@HiltViewModel
internal class SignUpViewModel @Inject constructor(
    private val checkIdUseCase: CheckIdUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state get() = _state.asStateFlow()

    val id = Account(WAIT_TIME)
    val password = Account(WAIT_TIME)
    val repeatPassword = Account(WAIT_TIME)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            id.checkIdValid {
                checkId(it)
            }.launchIn(viewModelScope)
        }
        password.checkValid { it.isValidPassword() }.launchIn(viewModelScope)
        repeatPassword.checkRepeatPasswordValid(password.value).launchIn(viewModelScope)
    }



    fun onEvent(event: SignUpEvent){
        when(event){
            SignUpEvent.NextStep -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update {
                        it.copy(isSuccess = password.now().isValidPassword()
                                && repeatPassword.now().passwordMatches(password.now())
                                && checkId(id.now()))
                    }
                }
            }
            is SignUpEvent.Id -> id.updateValue(event.value)
            is SignUpEvent.Password -> password.updateValue(event.value)
            is SignUpEvent.RepeatPassword -> repeatPassword.updateValue(event.value)
        }
    }

    private suspend fun checkId(id : String) : Boolean{
        var result = false
        viewModelScope.launch(Dispatchers.IO) {
            checkIdUseCase(id)
                .onSuccess { result = true }
                .onException { code, message ->
                    result = false
                    _state.update { it.copy(errorMessage = message) }
                }
        }
        return result
    }

    companion object {
        private const val WAIT_TIME = 800L
    }
}
