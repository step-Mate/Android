package jinproject.stepwalk.login.screen.signupdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import jinproject.stepwalk.login.utils.isValidDouble
import jinproject.stepwalk.login.utils.isValidInt
import jinproject.stepwalk.login.utils.isValidNickname
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.model.UserData
import jinproject.stepwalk.domain.model.onException
import jinproject.stepwalk.domain.model.onSuccess
import jinproject.stepwalk.domain.usecase.auth.RequestEmailCodeUseCase
import jinproject.stepwalk.domain.usecase.auth.SignUpUseCase
import jinproject.stepwalk.domain.usecase.auth.VerificationEmailCodeUseCase
import jinproject.stepwalk.login.screen.EmailViewModel
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.utils.isValidEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface SignUpDetailEvent{
    data object SignUp : SignUpDetailEvent
    data object RequestEmail : SignUpDetailEvent
    data class Nickname(val value : String) : SignUpDetailEvent
    data class Age(val value : String) : SignUpDetailEvent
    data class Height(val value : String) : SignUpDetailEvent
    data class Weight(val value : String) : SignUpDetailEvent
    data class Email(val value : String) : SignUpDetailEvent
    data class EmailCode(val value : String) : SignUpDetailEvent
}

@HiltViewModel
internal class SignUpDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    requestEmailCodeUseCase: RequestEmailCodeUseCase,
    private val verificationEmailCodeUseCase: VerificationEmailCodeUseCase,
    private val signUpUseCase: SignUpUseCase
) : EmailViewModel(requestEmailCodeUseCase){

    private var id = ""
    private var password = ""

    val nickname = Account(WAIT_TIME)
    val age = Account(WAIT_TIME)
    val height = Account(WAIT_TIME)
    val weight = Account(WAIT_TIME)

    init {
        id = savedStateHandle.get<String>("id") ?: ""
        password = savedStateHandle.get<String>("password") ?: ""
        nickname.checkValid { it.isValidNickname() }.launchIn(viewModelScope)
        age.checkValid { it.isValidInt() }.launchIn(viewModelScope)
        height.checkValid { it.isValidDouble() }.launchIn(viewModelScope)
        weight.checkValid { it.isValidDouble() }.launchIn(viewModelScope)
        email.checkValid { it.isValidEmail() }.launchIn(viewModelScope)
        viewModelScope.launch(Dispatchers.IO) {
            emailCode.checkEmailCodeValid {
                checkEmailVerification(requestEmail,it)
            }.launchIn(viewModelScope)
        }
    }
    
    fun onEvent(event : SignUpDetailEvent){
        when(event){
            SignUpDetailEvent.SignUp -> {
                if(nickname.isSuccessful() && age.isSuccessful() && height.isSuccessful() && weight.isSuccessful() &&
                    email.isSuccessful() && emailCode.isSuccessful()){
                    viewModelScope.launch(Dispatchers.IO) {
                        signUpUseCase(
                            UserData(
                                id = id,
                                password = password,
                                nickname = nickname.now(),
                                age = age.now().toInt(),
                                height = height.now().toInt(),
                                weight = weight.now().toInt(),
                                email = email.now()
                            )
                        )
                        .onSuccess {
                            _state.update { it.copy(isSuccess = true) }
                            //jwt save + user data
                        }
                        .onException{ code, message ->
                            _state.update { it.copy(errorMessage = message)}
                        }
                    }
                }else{
                    _state.update { it.copy(errorMessage = "입력조건이 잘못되었습니다.")}
                }
            }
            SignUpDetailEvent.RequestEmail -> {
                viewModelScope.launch(Dispatchers.IO) {
                    requestEmailVerification()
                }
            }
            is SignUpDetailEvent.Nickname -> nickname.updateValue(event.value)
            is SignUpDetailEvent.Age -> age.updateValue(event.value)
            is SignUpDetailEvent.Height -> height.updateValue(event.value)
            is SignUpDetailEvent.Weight -> weight.updateValue(event.value)
            is SignUpDetailEvent.Email -> email.updateValue(event.value)
            is SignUpDetailEvent.EmailCode -> emailCode.updateValue(event.value)
        }
    }

    private suspend fun checkEmailVerification(email : String, code : String) : Boolean{
        var result = false
        withContext(Dispatchers.IO){
            verificationEmailCodeUseCase(email, code)
                .onSuccess { result = true}
                .onException { code,message ->
                    result = false
                    if (code != 403)
                        _state.update { it.copy(errorMessage = message) }
            }
        }
        return result
    }
    
    companion object {
        private const val WAIT_TIME = 500L
    }
}
