package jinproject.stepwalk.login.screen.signup

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.login.screen.state.SignValid
import jinproject.stepwalk.login.screen.state.AccountValid
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
internal class SignUpViewModel @Inject constructor(

) : ViewModel() {
    private val _id = MutableStateFlow("")
    val id = _id.asStateFlow()

    private val _password = MutableStateFlow("")
    val password = _password.asStateFlow()

    private val _repeatPassword = MutableStateFlow("")
    val repeatPassword = _repeatPassword.asStateFlow()

    val valids = AccountValid()

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

    init {
        checkIdValid()
        checkPasswordValid()
        checkRepeatPasswordValid()
    }

    fun updateAccountEvent(event : AccountEvent, value : String){
        when(event){
            AccountEvent.id -> _id.value = value
            AccountEvent.password -> _password.value = value
            AccountEvent.repeatPassword -> _repeatPassword.value = value
        }
    }

    private fun checkIdValid() = debouncedIdFilter
        .onEach {
            valids.idValid.value = it?.let {
                when {
                    it.isBlank() -> SignValid.blank
                    !it.isValidID() -> SignValid.notValid
                    //서버에서 아이디 중복 요청 -> SignValid.duplicationId
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
        private const val WAIT_TIME = 800L
    }
}

enum class AccountEvent {
    id,password,repeatPassword
}

@Stable
data class SignUp(
    val id : String = "",
    val password : String = "",
    val repeatPassword : String = ""
)