package com.stepmate.login.screen.signupdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.stepmate.core.isValidNickname
import dagger.hilt.android.lifecycle.HiltViewModel
import com.stepmate.domain.model.SignUpData
import com.stepmate.domain.model.onException
import com.stepmate.domain.model.onSuccess
import com.stepmate.domain.usecase.auth.CheckNicknameUseCase
import com.stepmate.domain.usecase.auth.RequestEmailCodeUseCase
import com.stepmate.domain.usecase.auth.SignUpUseCase
import com.stepmate.domain.usecase.auth.VerificationEmailCodeUseCase
import com.stepmate.login.screen.EmailViewModel
import com.stepmate.login.screen.state.Account
import com.stepmate.login.screen.state.SignValid
import com.stepmate.core.isValidEmail
import com.stepmate.core.isValidEmailCode
import com.stepmate.login.utils.onEachState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
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
        email.checkValid { it.isValidEmail() }.launchIn(viewModelScope)
        viewModelScope.launch(Dispatchers.IO) {
            emailCode.checkValid(
                check = {it.isValidEmailCode()},
                suspendCheck = {checkEmailVerification(requestEmail,it)},
                suspendValid = SignValid.notValid
            ).launchIn(viewModelScope)
            nickname.checkValid(
                check = {it.isValidNickname()},
                suspendCheck = {checkDuplicationNickname(it)},
                suspendValid = SignValid.duplicationId
            ).launchIn(viewModelScope)
        }
    }
    
    fun onEvent(event : SignUpDetailEvent){
        when(event){
            SignUpDetailEvent.SignUp -> {
                if(nickname.isSuccessful() && emailCode.isSuccessful()){
                    viewModelScope.launch(Dispatchers.IO) {
                        signUpUseCase(
                            SignUpData(
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

    private suspend fun checkEmailVerification(email : String, code : String) : Boolean {
        var result = false
        verificationEmailCodeUseCase(email, code)
            .onSuccess { result = true }
            .onException { errorCode, message ->
                result = false
                if (errorCode != 403)
                    _state.update { it.copy(errorMessage = message) }
            }
        return result
    }

    private suspend fun checkDuplicationNickname(nickname: String) : Boolean {
        var result = false
        checkNicknameUseCase(nickname)
            .onSuccess { result = true }
            .onException { code, message ->
                result = false
                if (code != 432)
                    _state.update { it.copy(errorMessage = message) }
            }
        return result
    }

    companion object {
        private const val WAIT_TIME = 500L
    }
}
