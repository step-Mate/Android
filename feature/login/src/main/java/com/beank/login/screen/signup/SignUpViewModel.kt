package com.beank.login.screen.signup

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beank.login.utils.isValidID
import com.beank.login.utils.isValidPassword
import com.beank.login.utils.passwordMatches
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
internal class SignUpViewModel @Inject constructor(

) : ViewModel() {
    private val _id = MutableStateFlow("")
    val id = _id.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _repeatPassword = MutableStateFlow("")
    val repeatPassword = _repeatPassword.asStateFlow()

//    var idValid by mutableStateOf(SignValid.blank)
//    var passwordValid by mutableStateOf(SignValid.blank)
//    var repeatPasswordValid by mutableStateOf(SignValid.blank)

    val valids = ValidValue()

    private val debouncedIdFilter : Flow<String?> = id
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    private val debouncedPasswordFilter : Flow<String?> = password
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    private val debouncedRepeatPasswordFilter : Flow<String?> = repeatPassword
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    init {
        checkIdValid()
        checkPasswordValid()
        checkRepeatPasswordValid()
    }

    fun updateIdValue(id : String){
        _id.value = id
    }

    fun updatePasswordValue(password : String){
        _password.value = password
    }

    fun updateRepeatPasswordValue(repeatPassword : String){
        _repeatPassword.value = repeatPassword
    }

    fun checkIdValid() = debouncedIdFilter
        .onEach {
            valids.idValid = it?.let {
                when {
                    it.isBlank() -> SignValid.blank
                    !it.isValidID() -> SignValid.notValid
                    //서버에서 아이디 중복 요청 -> SignValid.duplicationId
                    else -> SignValid.success
                }
            } ?: SignValid.blank
        }.launchIn(viewModelScope)

    fun checkPasswordValid() = debouncedPasswordFilter
        .onEach {
            valids.passwordValid = it?.let {
                when {
                    it.isBlank() -> SignValid.blank
                    !it.isValidPassword() -> SignValid.notValid
                    else -> SignValid.success
                }
            } ?: SignValid.blank
        }.launchIn(viewModelScope)

    fun checkRepeatPasswordValid() = debouncedRepeatPasswordFilter
        .onEach {
            valids.repeatPasswordValid = it?.let {
                when {
                    it.isBlank() -> SignValid.blank
                    !it.isValidPassword() -> SignValid.notValid
                    !it.passwordMatches(password.value) -> SignValid.notMatch
                    else -> SignValid.success
                }
            } ?: SignValid.blank
        }.launchIn(viewModelScope)

    companion object {
        private const val WAIT_TIME = 1000L
    }
}

@Stable
internal class ValidValue(
    idValid : SignValid = SignValid.blank,
    passwordValid : SignValid = SignValid.blank,
    repeatPasswordValid : SignValid = SignValid.blank
){
    var idValid by mutableStateOf(idValid)
    var passwordValid by mutableStateOf(passwordValid)
    var repeatPasswordValid by mutableStateOf(repeatPasswordValid)
    fun isSuccessfulValid() : Boolean = (idValid == SignValid.success) and (passwordValid == SignValid.success) and (repeatPasswordValid == SignValid.success)
}

enum class SignValid {
    blank,notValid,duplicationId,notMatch,success
}

internal fun SignValid.isError() : Boolean = (this != SignValid.success) and (this != SignValid.blank)
