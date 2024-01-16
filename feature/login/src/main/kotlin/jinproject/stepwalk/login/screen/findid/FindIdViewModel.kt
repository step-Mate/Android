package jinproject.stepwalk.login.screen.findid

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.login.screen.FindViewModel
import jinproject.stepwalk.login.screen.state.SignValid
import jinproject.stepwalk.login.utils.isValidEmail
import kotlinx.coroutines.flow.launchIn
import javax.inject.Inject

@HiltViewModel
internal class FindIdViewModel @Inject constructor(

) : FindViewModel(){

    val id = mutableStateOf("")

    init {
        email.checkValid { it.isValidEmail() }.launchIn(viewModelScope)
        emailCode.checkValid { it -> true }.launchIn(viewModelScope)//추후에 서버에 이메일 코드 일치하는지로 교체
    }

    fun updateEmail(email : String){
        this.email.updateValue(email)
    }

    fun updateEmailCode(emailCode : String){
        this.emailCode.updateValue(emailCode)
    }

    override fun requestFindAccount() {
        //서버에 아이디 요청후 받아오기
        //받아오면 화면전환
        id.value = "test"
        nextStep.value = true
    }

    override fun requestEmailVerification() {
        //서버에 이메일 인증코드 보내도록 요청
        email.updateValid(SignValid.verifying)
        requestEmail = email.now()
    }
}
