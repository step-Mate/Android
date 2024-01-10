package jinproject.stepwalk.login.screen.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf

@Stable
internal class UserDataValid(
    nicknameValid : UserValid = UserValid.blank,
    ageValid : UserValid = UserValid.blank,
    heightValid : UserValid = UserValid.blank,
    weightValid : UserValid = UserValid.blank,
    emailValid : Verification = Verification.nothing
){
    val nicknameValid = mutableStateOf(nicknameValid)
    val ageValid = mutableStateOf(ageValid)
    val heightValid = mutableStateOf(heightValid)
    val weightValid = mutableStateOf(weightValid)
    val emailValid = mutableStateOf(emailValid)

    fun isSuccessfulValid() : Boolean =
        (nicknameValid.value == UserValid.success) and (ageValid.value == UserValid.success) and
                (heightValid.value == UserValid.success) and (weightValid.value == UserValid.success) and (emailValid.value == Verification.success)
}

enum class UserValid {
    blank,notValid,success
}

internal fun UserValid.isError() : Boolean =  this == UserValid.notValid