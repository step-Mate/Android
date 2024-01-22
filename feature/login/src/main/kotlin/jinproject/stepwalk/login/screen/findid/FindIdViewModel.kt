package jinproject.stepwalk.login.screen.findid

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.model.onException
import jinproject.stepwalk.domain.model.onSuccess
import jinproject.stepwalk.domain.usecase.auth.FindIdUseCase
import jinproject.stepwalk.domain.usecase.auth.RequestEmailCodeUseCase
import jinproject.stepwalk.domain.usecase.auth.VerificationEmailCodeUseCase
import jinproject.stepwalk.login.screen.EmailViewModel
import jinproject.stepwalk.login.utils.isValidEmail
import jinproject.stepwalk.login.utils.isValidEmailCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FindIdEvent{
    data object findId : FindIdEvent
    data object requestEmail : FindIdEvent
    data class email(val value : String) : FindIdEvent
    data class emailCode(val value: String) : FindIdEvent
}

@HiltViewModel
internal class FindIdViewModel @Inject constructor(
    requestEmailCodeUseCase: RequestEmailCodeUseCase,
    verificationEmailCodeUseCase: VerificationEmailCodeUseCase,
    private val findIdUseCase: FindIdUseCase
) : EmailViewModel(requestEmailCodeUseCase, verificationEmailCodeUseCase){

    var id by mutableStateOf("")

    init {
        email.checkValid { it.isValidEmail() }.launchIn(viewModelScope)
        emailCode.checkValid { it.isValidEmailCode()}.launchIn(viewModelScope)
    }

    fun onEvent(event: FindIdEvent) {
        when(event){
            FindIdEvent.findId -> {
                viewModelScope.launch(Dispatchers.IO) {
                    val response = findIdUseCase(requestEmail,emailCode.now().toInt())
                    response.onSuccess {findId ->
                        _state.update {
                            it.copy(isSuccess = true)
                        }
                        id = findId
                    }
                    response.onException { code, message ->
                        _state.update { it.copy(errorMessage = message) }
                    }
                }
            }
            FindIdEvent.requestEmail -> {
                viewModelScope.launch {
                    requestEmailVerification()
                }
            }
            is FindIdEvent.email -> email.updateValue(event.value)
            is FindIdEvent.emailCode -> emailCode.updateValue(event.value)
        }
    }
}
