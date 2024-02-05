package jinproject.stepwalk.login.screen.findpassword

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.model.onException
import jinproject.stepwalk.domain.model.onLoading
import jinproject.stepwalk.domain.model.onSuccess
import jinproject.stepwalk.domain.usecase.auth.RequestEmailCodeUseCase
import jinproject.stepwalk.domain.usecase.auth.ResetPasswordUseCase
import jinproject.stepwalk.domain.usecase.auth.VerificationUserEmailUseCase
import jinproject.stepwalk.login.screen.EmailViewModel
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.screen.state.SignValid
import jinproject.stepwalk.login.utils.isValidEmail
import jinproject.stepwalk.login.utils.isValidEmailCode
import jinproject.stepwalk.login.utils.isValidID
import jinproject.stepwalk.login.utils.isValidPassword
import jinproject.stepwalk.login.utils.passwordMatches
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FindPasswordEvent {
    data object ResetPassword : FindPasswordEvent
    data object RequestEmail : FindPasswordEvent
    data object CheckVerification : FindPasswordEvent
    data class Id(val value: String) : FindPasswordEvent
    data class Password(val value: String) : FindPasswordEvent
    data class RepeatPassword(val value: String) : FindPasswordEvent
    data class Email(val value: String) : FindPasswordEvent
    data class EmailCode(val value: String) : FindPasswordEvent
}

@HiltViewModel
internal class FindPasswordViewModel @Inject constructor(
    requestEmailCodeUseCase: RequestEmailCodeUseCase,
    private val verificationUserEmailUseCase: VerificationUserEmailUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase
) : EmailViewModel(requestEmailCodeUseCase) {
    private val _nextStep = MutableStateFlow(false)
    val nextStep get() = _nextStep.asStateFlow()

    val id = Account(WAIT_TIME)
    val password = Account(WAIT_TIME)
    val repeatPassword = Account(WAIT_TIME)

    init {
        id.checkValid { it.isValidID() }.launchIn(viewModelScope)
        password.checkValid { it.isValidPassword() }.launchIn(viewModelScope)
        repeatPassword.checkValid(
            checkValid = SignValid.notMatch,
            check = { it.passwordMatches(password.now()) }
        ).launchIn(viewModelScope)
        email.checkValid { it.isValidEmail() }.launchIn(viewModelScope)
        emailCode.checkValid { it.isValidEmailCode() }.launchIn(viewModelScope)
    }

    fun onEvent(event: FindPasswordEvent) {
        when (event) {
            FindPasswordEvent.ResetPassword -> {
                viewModelScope.launch(Dispatchers.IO) {
                    resetPasswordUseCase(id.now(), password.now())
                        .onSuccess {
                            _state.update {
                                it.copy(isSuccess = true)
                            }
                        }
                        .onException { code, message ->
                            _state.update { it.copy(errorMessage = message) }
                        }
                }
            }

            FindPasswordEvent.CheckVerification -> {
                viewModelScope.launch(Dispatchers.IO) {
                    verificationUserEmailUseCase(id.now(), requestEmail, emailCode.now())
                        .onEach {
                            it.onSuccess {
                                _nextStep.value = true
                                _state.update { state -> state.copy(isLoading = false) }
                            }.onException { code, message ->
                                if (code != 403)
                                    _state.update {
                                        it.copy(
                                            errorMessage = message,
                                            isLoading = false
                                        )
                                    }
                                else
                                    _state.update { state -> state.copy(isLoading = false) }
                            }.onLoading {
                                _state.update { state -> state.copy(isLoading = false) }
                            }
                        }.launchIn(viewModelScope)
                }
            }

            FindPasswordEvent.RequestEmail -> {
                viewModelScope.launch(Dispatchers.IO) {
                    requestEmailVerification()
                }
            }

            is FindPasswordEvent.Id -> id.updateValue(event.value)
            is FindPasswordEvent.Password -> password.updateValue(event.value)
            is FindPasswordEvent.RepeatPassword -> repeatPassword.updateValue(event.value)
            is FindPasswordEvent.Email -> email.updateValue(event.value)
            is FindPasswordEvent.EmailCode -> emailCode.updateValue(event.value)
        }
    }

    companion object {
        private const val WAIT_TIME = 500L
    }
}
