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
import jinproject.stepwalk.login.screen.EmailViewModel
import jinproject.stepwalk.login.utils.isValidEmail
import jinproject.stepwalk.login.utils.isValidEmailCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FindIdEvent{
    data object FindId : FindIdEvent
    data object RequestEmail : FindIdEvent
    data class Email(val value : String) : FindIdEvent
    data class EmailCode(val value: String) : FindIdEvent
}

@HiltViewModel
internal class FindIdViewModel @Inject constructor(
    requestEmailCodeUseCase: RequestEmailCodeUseCase,
    private val findIdUseCase: FindIdUseCase
) : EmailViewModel(requestEmailCodeUseCase){

    var id by mutableStateOf("")

    init {
        email.checkValid { it.isValidEmail() }.launchIn(viewModelScope)
        emailCode.checkValid { it.isValidEmailCode()}.launchIn(viewModelScope)
    }

    fun onEvent(event: FindIdEvent) {
        when(event){
            FindIdEvent.FindId -> {
                viewModelScope.launch(Dispatchers.IO) {
                    findIdUseCase(requestEmail,emailCode.now())
                        .onSuccess {findId ->
                            _state.update {
                                it.copy(isSuccess = true)
                            }
                            id = findId
                        }
                        .onException { code, message ->
                            _state.update { it.copy(errorMessage = message) }
                        }
                }
            }
            FindIdEvent.RequestEmail -> {
                viewModelScope.launch {
                    requestEmailVerification()
                }
            }
            is FindIdEvent.Email -> email.updateValue(event.value)
            is FindIdEvent.EmailCode -> emailCode.updateValue(event.value)
        }
    }
}
