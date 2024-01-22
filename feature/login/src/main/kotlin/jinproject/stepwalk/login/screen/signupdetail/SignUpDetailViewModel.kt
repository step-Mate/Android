package jinproject.stepwalk.login.screen.signupdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
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
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.screen.state.AuthState
import jinproject.stepwalk.login.screen.state.SignValid
import jinproject.stepwalk.login.utils.isValidEmail
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface SignUpDetailEvent{
    data object signUp : SignUpDetailEvent
    data object requestEmail : SignUpDetailEvent
    data class nickname(val value : String) : SignUpDetailEvent
    data class age(val value : String) : SignUpDetailEvent
    data class height(val value : String) : SignUpDetailEvent
    data class weight(val value : String) : SignUpDetailEvent
    data class email(val value : String) : SignUpDetailEvent
    data class emailCode(val value : String) : SignUpDetailEvent
}

@HiltViewModel
internal class SignUpDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val requestEmailCodeUseCase: RequestEmailCodeUseCase,
    private val verificationEmailCodeUseCase: VerificationEmailCodeUseCase,
    private val signUpUseCase: SignUpUseCase
) : ViewModel(){

    private var id = ""
    private var password = ""
    private var requestEmail = ""

    private val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    val nickname = Account(WAIT_TIME)
    val age = Account(WAIT_TIME)
    val height = Account(WAIT_TIME)
    val weight = Account(WAIT_TIME)
    val email = Account(WAIT_TIME)
    val emailCode = Account(1000)

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
                checkEmailVerification(requestEmail,it.toInt())
            }.launchIn(viewModelScope)
        }
    }
    
    fun onEvent(event : SignUpDetailEvent){
        when(event){
            SignUpDetailEvent.signUp -> {
                if(nickname.isSuccessful() && age.isSuccessful() && height.isSuccessful() && weight.isSuccessful() &&
                    email.isSuccessful() && emailCode.isSuccessful()){
                    viewModelScope.launch(Dispatchers.IO) {
                        val response = signUpUseCase(
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
                        response.onSuccess {
                            _state.update { it.copy(isSuccess = true) }
                            //jwt save + user data
                        }
                        response.onException{ code, message ->
                            _state.update { it.copy(errorMessage = message)}
                        }
                    }
                }else{
                    _state.update { it.copy(errorMessage = "입력조건이 잘못되었습니다.")}
                }
            }
            SignUpDetailEvent.requestEmail -> {
                email.updateValid(SignValid.verifying)
                requestEmail = email.now()
                viewModelScope.launch(Dispatchers.IO) {
                    val response = requestEmailCodeUseCase(requestEmail)
                    response.onException { _, message ->
                        _state.update { it.copy(errorMessage = message) }
                    }
                }
            }
            is SignUpDetailEvent.nickname -> nickname.updateValue(event.value)
            is SignUpDetailEvent.age -> age.updateValue(event.value)
            is SignUpDetailEvent.height -> height.updateValue(event.value)
            is SignUpDetailEvent.weight -> weight.updateValue(event.value)
            is SignUpDetailEvent.email -> email.updateValue(event.value)
            is SignUpDetailEvent.emailCode -> emailCode.updateValue(event.value)
        }
    }

    private suspend fun checkEmailVerification(email : String, code : Int) : Boolean{
        var result = false
        withContext(Dispatchers.IO){
            val response = verificationEmailCodeUseCase(email, code)
            response.onSuccess {
                result = true
            }
            response.onException { code,message ->
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
