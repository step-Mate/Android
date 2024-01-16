package jinproject.stepwalk.login.screen.findpassword

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.login.screen.FindViewModel
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.screen.state.SignValid
import jinproject.stepwalk.login.utils.isValidEmail
import jinproject.stepwalk.login.utils.isValidID
import jinproject.stepwalk.login.utils.isValidPassword
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@HiltViewModel
internal class FindPasswordViewModel @Inject constructor(

) : FindViewModel(){

    val id = Account(WAIT_TIME)
    val password = Account(WAIT_TIME)
    val repeatPassword = Account(WAIT_TIME)

    init {
        id.checkValid { it.isValidID() }.launchIn(viewModelScope)//안에 아이디 서버 중복 체크하는거 추가
        password.checkValid { it.isValidPassword() }
        repeatPassword.checkRepeatPasswordValid(password.now())
        email.checkValid { it.isValidEmail() }.launchIn(viewModelScope)
        emailCode.checkEmailCodeValid { it -> true }.launchIn(viewModelScope)//추후에 서버에 이메일 코드 일치하는지로 교체
    }

    fun updateFindEvent(event: FindEvent, value : String){
        when(event){
            FindEvent.id -> id.updateValue(value)
            FindEvent.email -> email.updateValue(value)
            FindEvent.emailCode -> emailCode.updateValue(value)
            FindEvent.password -> password.updateValue(value)
            FindEvent.repeatPassword -> repeatPassword.updateValue(value)
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
        emailCode.updateValid(SignValid.verifying)
        requestEmail = email.now()
    }

    companion object {
        private const val WAIT_TIME = 500L
    }
}

enum class FindEvent{
    id,email,emailCode,password,repeatPassword
}
