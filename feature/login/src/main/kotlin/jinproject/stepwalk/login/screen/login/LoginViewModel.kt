package jinproject.stepwalk.login.screen.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jinproject.stepwalk.login.utils.isValidID
import jinproject.stepwalk.login.utils.isValidPassword
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.usecase.auth.SignInUseCase
import jinproject.stepwalk.login.screen.state.AuthState
import jinproject.stepwalk.login.utils.onEachState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class LoginViewModel @Inject constructor(
    private val signInUseCase: SignInUseCase,
) : ViewModel() {

    private val _state = MutableStateFlow(AuthState())
    val state get() = _state.asStateFlow()

    fun checkValidAccount(id: String, password: String) {
        when {
            id.isBlank() -> _state.update { it.copy(errorMessage = "아이디를 입력해주세요.") }
            !id.isValidID() -> _state.update { it.copy(errorMessage = "잘못된 아이디 양식이에요. 영어,숫자,_만 입력가능하고 4~12글자까지 입력가능해요.") }
            password.isBlank() -> _state.update { it.copy(errorMessage = "비밀번호를 입력해주세요.") }
            !password.isValidPassword() -> _state.update { it.copy(errorMessage = "잘못된 비밀번호 양식이에요. 8~16글자까지 입력가능하고 영어,숫자,!@#\$%^&amp;*특수문자만 사용가능해요.") }
            else -> {
                viewModelScope.launch(Dispatchers.IO) {
                    signInUseCase(id, password)
                        .onEachState(_state)
                        .launchIn(viewModelScope)
                }
            }
        }

    }
}