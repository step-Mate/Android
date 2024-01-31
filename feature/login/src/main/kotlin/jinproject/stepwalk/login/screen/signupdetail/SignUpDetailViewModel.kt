package jinproject.stepwalk.login.screen.signupdetail

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import jinproject.stepwalk.login.utils.isValidNickname
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.model.UserData
import jinproject.stepwalk.domain.model.onException
import jinproject.stepwalk.domain.model.onSuccess
import jinproject.stepwalk.domain.usecase.auth.CheckNicknameUseCase
import jinproject.stepwalk.domain.usecase.auth.RequestEmailCodeUseCase
import jinproject.stepwalk.domain.usecase.auth.SignUpUseCase
import jinproject.stepwalk.domain.usecase.auth.VerificationEmailCodeUseCase
import jinproject.stepwalk.login.screen.EmailViewModel
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.screen.state.SignValid
import jinproject.stepwalk.login.utils.isValidEmail
import jinproject.stepwalk.login.utils.isValidEmailCode
import jinproject.stepwalk.login.utils.onEachState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

sealed interface SignUpDetailEvent{
    data object SignUp : SignUpDetailEvent
    data object RequestEmail : SignUpDetailEvent
    data class Nickname(val value : String) : SignUpDetailEvent
    data class Email(val value : String) : SignUpDetailEvent
    data class EmailCode(val value : String) : SignUpDetailEvent
}

@HiltViewModel
internal class SignUpDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    requestEmailCodeUseCase: RequestEmailCodeUseCase,
    private val verificationEmailCodeUseCase: VerificationEmailCodeUseCase,
    private val checkNicknameUseCase: CheckNicknameUseCase,
    private val signUpUseCase: SignUpUseCase
) : EmailViewModel(requestEmailCodeUseCase){

    private var id = ""
    private var password = ""

    val nickname = Account(WAIT_TIME)

    init {
        id = savedStateHandle.get<String>("id") ?: ""
        password = savedStateHandle.get<String>("password") ?: ""
        Log.d("id",id)
        nickname.checkValid { it.isValidNickname() }.launchIn(viewModelScope)//제거
        email.checkValid { it.isValidEmail() }.launchIn(viewModelScope)
        viewModelScope.launch(Dispatchers.IO) {
            emailCode.checkValids(
                check = {it.isValidEmailCode()},
                suspendCheck = {checkEmailVerification(requestEmail,it)},
                suspendValid = SignValid.notValid
            ).launchIn(viewModelScope)
//            nickname.checkValids(
//                check = {it.isValidNickname()},
//                suspendCheck = {checkDuplicationNickname(it)},
//                suspendValid = SignValid.duplicationId
//            ).launchIn(viewModelScope)
        }
    }
    
    fun onEvent(event : SignUpDetailEvent){
        when(event){
            SignUpDetailEvent.SignUp -> {
                if(nickname.isSuccessful() && emailCode.isSuccessful()){
                    viewModelScope.launch(Dispatchers.IO) {
                        signUpUseCase(
                            UserData(
                                id = id,
                                password = password,
                                nickname = nickname.now(),
                                email = email.now()
                            )
                        ).onEachState(_state)
                        .launchIn(viewModelScope)
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
            is SignUpDetailEvent.Email -> email.updateValue(event.value)
            is SignUpDetailEvent.EmailCode -> emailCode.updateValue(event.value)
        }
    }

    private suspend fun checkEmailVerification(email : String, code : String) : Boolean = withContext(Dispatchers.IO){
        var result = false
        val job = async {
            verificationEmailCodeUseCase(email, code)
                .onSuccess { result = true }
                .onException { code, message ->
                    result = false
                    if (code != 403)
                        _state.update { it.copy(errorMessage = message) }
                }
        }
        job.await()
        return@withContext result
    }

    private suspend fun checkDuplicationNickname(nickname: String) : Boolean = withContext(Dispatchers.IO){
        var result = false
        val job = async {
            checkNicknameUseCase(nickname)
                .onSuccess { result = true }
                .onException { code, message ->
                    result = false
                    //if추가로 중복 오류 메시지 제거
                    _state.update { it.copy(errorMessage = message) }
                }
        }
        job.await()
        return@withContext result
    }
    
    companion object {
        private const val WAIT_TIME = 500L
    }
}
