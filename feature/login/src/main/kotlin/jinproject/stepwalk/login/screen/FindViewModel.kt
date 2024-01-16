package jinproject.stepwalk.login.screen

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import jinproject.stepwalk.login.screen.state.Account

abstract class FindViewModel(

) : ViewModel(){
    val email = Account(800)
    val emailCode = Account(1000)

    val nextStep = mutableStateOf(false)
    protected var requestEmail = ""
    abstract fun requestFindAccount()
    abstract fun requestEmailVerification()

}
