package jinproject.stepwalk.login.screen.signupdetail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jinproject.stepwalk.login.utils.isValidDouble
import jinproject.stepwalk.login.utils.isValidInt
import jinproject.stepwalk.login.utils.isValidNickname
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.screen.state.SignValid
import jinproject.stepwalk.login.utils.isValidEmail
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@HiltViewModel
internal class SignUpDetailViewModel @Inject constructor(
    private val savedStateHandle: SavedStateHandle
) : ViewModel(){

    private var id = ""
    private var password = ""
    private var requestEmail = ""

    val nickname = Account(WAIT_TIME)
    val age = Account(WAIT_TIME)
    val height = Account(WAIT_TIME)
    val weight = Account(WAIT_TIME)
    val email = Account(WAIT_TIME)
    val emailCode = Account(WAIT_TIME)

    init {
        id = savedStateHandle.get<String>("id") ?: ""
        password = savedStateHandle.get<String>("password") ?: ""
        nickname.checkValid { it.isValidNickname() }.launchIn(viewModelScope)
        age.checkValid { it.isValidInt() }.launchIn(viewModelScope)
        height.checkValid { it.isValidDouble() }.launchIn(viewModelScope)
        weight.checkValid { it.isValidDouble() }.launchIn(viewModelScope)
        email.checkValid { it.isValidEmail() }.launchIn(viewModelScope)
        emailCode.checkEmailCodeValid { it -> true }.launchIn(viewModelScope)//추후에 서버에 이메일 코드 일치하는지로 교체
    }

    fun updateUserEvent(event : UserEvent, value : String){
        when (event){
            UserEvent.nickname -> nickname.updateValue(value)
            UserEvent.age -> age.updateValue(value)
            UserEvent.height -> height.updateValue(value)
            UserEvent.weight -> weight.updateValue(value)
            UserEvent.email -> email.updateValue(value)
            UserEvent.emailCode -> emailCode.updateValue(value)
        }
    }

    fun requestEmailVerification(){
        email.updateValid(SignValid.verifying)
        requestEmail = email.now()
        //서버에 이메일 인증코드 보내도록 요청
    }

    companion object {
        private const val WAIT_TIME = 500L
    }
}

enum class UserEvent {
    nickname,age,height,weight,email,emailCode
}
