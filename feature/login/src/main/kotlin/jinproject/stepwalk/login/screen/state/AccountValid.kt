package jinproject.stepwalk.login.screen.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf

@Stable
internal class AccountValid(
    idValid : SignValid = SignValid.blank,
    passwordValid : SignValid = SignValid.blank,
    repeatPasswordValid : SignValid = SignValid.blank
){
    val idValid = mutableStateOf(idValid)
    val passwordValid = mutableStateOf(passwordValid)
    val repeatPasswordValid = mutableStateOf(repeatPasswordValid)
    fun isSuccessfulValid() : Boolean = (idValid.value == SignValid.success) and (passwordValid.value == SignValid.success) and (repeatPasswordValid.value == SignValid.success)
}

enum class SignValid {
    blank,notValid,duplicationId,notMatch,success
}

internal fun SignValid.isError() : Boolean = (this != SignValid.success) and (this != SignValid.blank)