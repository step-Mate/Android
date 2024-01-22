package jinproject.stepwalk.login.screen.findpassword

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.model.onException
import jinproject.stepwalk.domain.model.onSuccess
import jinproject.stepwalk.domain.usecase.auth.RequestEmailCodeUseCase
import jinproject.stepwalk.domain.usecase.auth.ResetPasswordUseCase
import jinproject.stepwalk.domain.usecase.auth.VerificationEmailCodeUseCase
import jinproject.stepwalk.login.screen.EmailViewModel
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.utils.isValidEmail
import jinproject.stepwalk.login.utils.isValidEmailCode
import jinproject.stepwalk.login.utils.isValidID
import jinproject.stepwalk.login.utils.isValidPassword
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FindPasswordEvent{
    data object resetPassword : FindPasswordEvent
    data object requestEmail : FindPasswordEvent
    data object checkVerification : FindPasswordEvent
    data class id(val value: String) : FindPasswordEvent
    data class password(val value: String) : FindPasswordEvent
    data class repeatPassword(val value: String) : FindPasswordEvent
    data class email(val value : String) : FindPasswordEvent
    data class emailCode(val value: String) : FindPasswordEvent
}

@HiltViewModel
internal class FindPasswordViewModel @Inject constructor(
    requestEmailCodeUseCase: RequestEmailCodeUseCase,
    verificationEmailCodeUseCase: VerificationEmailCodeUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : EmailViewModel(requestEmailCodeUseCase, verificationEmailCodeUseCase){
    private val _nextStep = MutableStateFlow(false)
    val nextStep = _nextStep.asStateFlow()

    val id = Account(WAIT_TIME)
    val password = Account(WAIT_TIME)
    val repeatPassword = Account(WAIT_TIME)

    init {
        id.checkValid { it.isValidID() }.launchIn(viewModelScope)
        password.checkValid { it.isValidPassword() }.launchIn(viewModelScope)
        repeatPassword.checkRepeatPasswordValid(password.now()).launchIn(viewModelScope)
        email.checkValid { it.isValidEmail() }.launchIn(viewModelScope)
        emailCode.checkValid { it.isValidEmailCode() }.launchIn(viewModelScope)
    }

    fun onEvent(event: FindPasswordEvent) {
        when(event){
            FindPasswordEvent.resetPassword -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val response = resetPasswordUseCase(id.now(),password.now())
                    response.onSuccess {findId ->
                        _state.update {
                            it.copy(isSuccess = true)
                        }
                    }
                    response.onException { code, message ->
                        _state.update { it.copy(errorMessage = message) }
                    }
                }
            }
            FindPasswordEvent.checkVerification -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val result = checkEmailVerification(requestEmail,emailCode.now().toInt())
                    _nextStep.value = result
                }
            }
            FindPasswordEvent.requestEmail -> {
                viewModelScope.launch(Dispatchers.IO) {
                    requestEmailVerification()
                }
            }
            is FindPasswordEvent.id -> id.updateValue(event.value)
            is FindPasswordEvent.password -> password.updateValue(event.value)
            is FindPasswordEvent.repeatPassword -> repeatPassword.updateValue(event.value)
            is FindPasswordEvent.email -> email.updateValue(event.value)
            is FindPasswordEvent.emailCode -> emailCode.updateValue(event.value)
        }
    }

    companion object {
        private const val WAIT_TIME = 500L
    }
}
