package jinproject.stepwalk.login.screen.signup

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.utils.isValidPassword
import jinproject.stepwalk.login.utils.passwordMatches
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject



@HiltViewModel
internal class SignUpViewModel @Inject constructor(

) : ViewModel() {
    val id = Account(WAIT_TIME)
    val password = Account(WAIT_TIME)
    val repeatPassword = Account(WAIT_TIME)


    init {
        id.checkIdValid { _ -> true }.launchIn(viewModelScope)//안에 아이디 서버 중복 체크하는거 추가
        password.checkValid { it.isValidPassword() }.launchIn(viewModelScope)
        repeatPassword.checkRepeatPasswordValid(password.now()).launchIn(viewModelScope)
    }

    fun updateAccountEvent(event : AccountEvent, value : String){
        when(event){
            AccountEvent.id -> id.updateValue(value)
            AccountEvent.password -> password.updateValue(value)
            AccountEvent.repeatPassword -> repeatPassword.updateValue(value)
        }
    }

    fun checkAccountValid() : Boolean =
        password.now().isValidPassword() && repeatPassword.now().passwordMatches(password.now()) //서버에 아이디도 중복인지 한번더 체크

    companion object {
        private const val WAIT_TIME = 800L
    }
}

enum class AccountEvent {
    id,password,repeatPassword
}
