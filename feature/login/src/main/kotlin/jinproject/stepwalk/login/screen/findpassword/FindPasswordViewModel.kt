package jinproject.stepwalk.login.screen.findpassword

import androidx.compose.runtime.Stable
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.login.screen.FindViewModel
import jinproject.stepwalk.login.screen.state.SignValid
import jinproject.stepwalk.login.screen.state.AccountValid
import jinproject.stepwalk.login.screen.state.Verification
import jinproject.stepwalk.login.utils.isValidID
import jinproject.stepwalk.login.utils.isValidPassword
import jinproject.stepwalk.login.utils.passwordMatches
import kotlinx.coroutines.FlowPreview
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
internal class FindPasswordViewModel @Inject constructor(

) : FindViewModel(){

    private val _id = MutableStateFlow("")
    val id = _id.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _repeatPassword = MutableStateFlow("")
    val repeatPassword = _repeatPassword.asStateFlow()

    val valids = AccountValid()

    init {
        checkEmailValid()
        checkEmailCodeValid()
        checkIdValid()
        checkPasswordValid()
        checkRepeatPasswordValid()
    }

    fun updateFindEvent(event: FindEvent, value : String){
        when(event){
            FindEvent.id -> _id.value = value
            FindEvent.email -> _email.value = value
            FindEvent.emailCode -> _emailCode.value = value
            FindEvent.password -> _password.value = value
            FindEvent.repeatPassword -> _repeatPassword.value = value
        }
    }

    fun requestResetPassword(){

    }

    override fun requestFindAccount() {
        //인증코드와 이메일 일치하는지 요청
        nextStep.value = true//비번 재설정 화면으로 전환
    }

    override fun requestEmailVerification() {
        //서버에 아이디와 이메일이 일치하는지 확인후 요청코드 보내기
        emailValid.value = Verification.verifying
        requestEmail = email.value
    }

    @OptIn(FlowPreview::class)
    private val debouncedIdFilter : Flow<String?> = id
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    @OptIn(FlowPreview::class)
    private val debouncedPasswordFilter : Flow<String?> = password
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    @OptIn(FlowPreview::class)
    private val debouncedRepeatPasswordFilter : Flow<String?> = repeatPassword
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    private fun checkIdValid() = debouncedIdFilter
        .onEach {
            valids.idValid.value = it?.let {
                when {
                    it.isBlank() -> SignValid.blank
                    !it.isValidID() -> SignValid.notValid
                    else -> SignValid.success
                }
            } ?: SignValid.blank
        }.launchIn(viewModelScope)

    private fun checkPasswordValid() = debouncedPasswordFilter
        .onEach {
            valids.passwordValid.value = it?.let {
                when {
                    it.isBlank() -> SignValid.blank
                    !it.isValidPassword() -> SignValid.notValid
                    else -> SignValid.success
                }
            } ?: SignValid.blank
        }.launchIn(viewModelScope)

    private fun checkRepeatPasswordValid() = debouncedRepeatPasswordFilter
        .onEach {
            valids.repeatPasswordValid.value = it?.let {
                when {
                    it.isBlank() -> SignValid.blank
                    !it.isValidPassword() -> SignValid.notValid
                    !it.passwordMatches(password.value) -> SignValid.notMatch
                    else -> SignValid.success
                }
            } ?: SignValid.blank
        }.launchIn(viewModelScope)

    companion object {
        private const val WAIT_TIME = 500L
    }
}

enum class FindEvent{
    id,email,emailCode,password,repeatPassword
}

@Stable
data class FindAccountPassword(
    val id : String = "",
    val email : String = "",
    val emailCode : String = "",
    val password : String = "",
    val repeatPassword : String = "",
    val emailValid : Verification = Verification.nothing,
    val nextStep : Boolean = false,
)