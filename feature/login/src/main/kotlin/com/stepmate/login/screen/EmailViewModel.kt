package com.stepmate.login.screen

import androidx.lifecycle.ViewModel
import com.stepmate.domain.model.onException
import com.stepmate.domain.usecase.auth.RequestEmailCodeUseCase
import com.stepmate.login.screen.state.Account
import com.stepmate.login.screen.state.AuthState
import com.stepmate.login.screen.state.SignValid
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update


abstract class EmailViewModel(
    private val requestEmailCodeUseCase: RequestEmailCodeUseCase,
) : ViewModel() {
    val email = Account(800)
    val emailCode = Account(1000)

    protected val _state = MutableStateFlow(AuthState())
    val state = _state.asStateFlow()

    protected var requestEmail = ""

    protected suspend fun requestEmailVerification() {
        email.updateValid(SignValid.verifying)
        requestEmail = email.now()
        requestEmailCodeUseCase(requestEmail).onException { _, message ->
            _state.update { it.copy(errorMessage = message) }
            email.updateValid(SignValid.notValid)
        }
    }
}
