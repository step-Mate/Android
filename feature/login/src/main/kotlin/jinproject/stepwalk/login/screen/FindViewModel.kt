package jinproject.stepwalk.login.screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jinproject.stepwalk.login.screen.state.Verification
import jinproject.stepwalk.login.utils.isValidEmail
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class FindViewModel(

) : ViewModel(){
    protected val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    protected val _emailCode = MutableStateFlow("")
    val emailCode = _emailCode.asStateFlow()

    val emailValid = mutableStateOf(Verification.nothing)
    val nextStep = mutableStateOf(false)
    protected var requestEmail = ""
    abstract fun requestFindAccount()
    abstract fun requestEmailVerification()

    @OptIn(FlowPreview::class)
    private val debouncedEmailFilter : Flow<String?> = email
        .debounce(500)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    @OptIn(FlowPreview::class)
    private val debouncedEmailCodeFilter : Flow<String?> = emailCode
        .debounce(1000)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    protected fun checkEmailValid() = debouncedEmailFilter
        .onEach {
            emailValid.value = it?.let {
                when {
                    it.isBlank() -> Verification.nothing
                    !it.isValidEmail() -> Verification.emailError
                    else -> Verification.emailValid
                }
            } ?: Verification.nothing
        }.launchIn(viewModelScope)

    protected fun checkEmailCodeValid() = debouncedEmailCodeFilter
        .onEach {
            emailValid.value = it?.let {
                when {
                    it.isBlank() -> Verification.verifying
                    //!it. -> Verification.codeError//서버에 code가 일치하는지 확인
                    else -> Verification.success
                }
            } ?: Verification.verifying
        }.launchIn(viewModelScope)

}
