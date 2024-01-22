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
    data object nextStep : SignUpEvent
    data class id(val value : String) : SignUpEvent
    data class password(val value : String) : SignUpEvent
    data class repeatPassword(val value : String) : SignUpEvent
}

@HiltViewModel
internal class SignUpViewModel @Inject constructor(
    private val checkIdUseCase: CheckIdUseCase
) : ViewModel() {
    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    val id = Account(WAIT_TIME)
    val password = Account(WAIT_TIME)
    val repeatPassword = Account(WAIT_TIME)

    init {
        viewModelScope.launch(Dispatchers.IO) {
            id.checkIdValid {
                checkId(it)
            }.launchIn(viewModelScope)//안에 아이디 서버 중복 체크하는거 추가
        }
        password.checkValid { it.isValidPassword() }.launchIn(viewModelScope)
        repeatPassword.checkRepeatPasswordValid(password.now()).launchIn(viewModelScope)
    }



    fun onEvent(event: SignUpEvent){
        when(event){
            SignUpEvent.nextStep -> {
                viewModelScope.launch(Dispatchers.IO) {
                    _state.update {
                        it.copy(isSuccess = password.now().isValidPassword()
                                && repeatPassword.now().passwordMatches(password.now())
                                && checkId(id.now()))
                    }
                }
            }
            is SignUpEvent.id -> id.updateValue(event.value)
            is SignUpEvent.password -> password.updateValue(event.value)
            is SignUpEvent.repeatPassword -> repeatPassword.updateValue(event.value)
        }
    }

    private suspend fun checkId(id : String) : Boolean{
        var result = false
        viewModelScope.launch(Dispatchers.IO) {
            val response = checkIdUseCase(id)
            response.onSuccess {
                result = true
            }
            response.onException { code, message ->
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
