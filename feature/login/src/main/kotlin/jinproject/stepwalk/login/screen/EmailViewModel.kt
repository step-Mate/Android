package jinproject.stepwalk.login.screen

import androidx.lifecycle.ViewModel
import jinproject.stepwalk.domain.model.onException
import jinproject.stepwalk.domain.usecase.auth.RequestEmailCodeUseCase
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.screen.state.AuthState
import jinproject.stepwalk.login.screen.state.SignValid
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.withContext


abstract class EmailViewModel(
    private val requestEmailCodeUseCase: RequestEmailCodeUseCase,
) : ViewModel(){
    val email = Account(800)
    val emailCode = Account(1000)

    protected val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    protected var requestEmail = ""

    protected suspend fun requestEmailVerification(){
        email.updateValid(SignValid.verifying)
        requestEmail = email.now()
        withContext(Dispatchers.IO) {
            requestEmailCodeUseCase(requestEmail).onException { _, message ->
                _state.update { it.copy(errorMessage = message) }
                email.updateValid(SignValid.notValid)
            }
        }
    }
}
