package jinproject.stepwalk.login.screen

import androidx.lifecycle.ViewModel
import jinproject.stepwalk.login.utils.isValidID
import jinproject.stepwalk.login.utils.isValidPassword

import dagger.hilt.android.lifecycle.HiltViewModel
import javax.inject.Inject


@HiltViewModel
internal class LoginViewModel @Inject constructor(

) : ViewModel() {

    fun checkValidAccount(id : String, password : String) : Valid = when{
            id.isBlank() -> Valid.ID_BLANK
            !id.isValidID() -> Valid.ID_NOT_VALID
            password.isBlank() -> Valid.PASS_BLANK
            !password.isValidPassword() -> Valid.PASS_NOT_VALID
            else -> {
                if (true)
                    Valid.ACCOUNT_VALID
                else
                    Valid.ACCOUNT_NOT_VALID
            }//로그인 정보 서버에 전송후 토큰 확인시 넘김처리
        }
}

internal enum class Valid {
   ID_BLANK, ID_NOT_VALID, PASS_BLANK, PASS_NOT_VALID, ACCOUNT_NOT_VALID, ACCOUNT_VALID
}