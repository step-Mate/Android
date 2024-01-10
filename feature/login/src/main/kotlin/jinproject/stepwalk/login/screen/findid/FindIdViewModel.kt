package jinproject.stepwalk.login.screen.findid

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.login.screen.FindViewModel
import jinproject.stepwalk.login.screen.state.Verification
import javax.inject.Inject

@HiltViewModel
internal class FindIdViewModel @Inject constructor(

) : FindViewModel(){

    val id = mutableStateOf("")

    init {
        checkEmailValid()
        checkEmailCodeValid()
    }

    fun updateEmail(email : String){
        _email.value = email
    }

    fun updateEmailCode(emailCode : String){
        _emailCode.value = emailCode
    }

    override fun requestFindAccount() {
        //서버에 아이디 요청후 받아오기
        //받아오면 화면전환
        id.value = "test"
        nextStep.value = true
    }

    override fun requestEmailVerification() {
        //서버에 이메일 인증코드 보내도록 요청
        emailValid.value = Verification.verifying
        requestEmail = email.value
    }
}

@Stable
data class FindAccountId(
    val email : String = "",
    val emailCode : String = "",
    val emailValid : Verification = Verification.nothing,
    val nextStep : Boolean = false,
    val id : String = ""
)