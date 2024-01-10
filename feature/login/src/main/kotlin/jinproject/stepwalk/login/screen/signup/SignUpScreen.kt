package jinproject.stepwalk.login.screen.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.component.EnableButton
import jinproject.stepwalk.login.component.IdDetail
import jinproject.stepwalk.login.component.PasswordDetail
import jinproject.stepwalk.login.utils.MAX_ID_LENGTH
import jinproject.stepwalk.login.utils.MAX_PASS_LENGTH
import jinproject.stepwalk.design.R.string as AppText

@Composable
internal fun SignUpScreen(
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    navigateToSignUpDetail : (String,String) -> Unit
) {
    val id by signUpViewModel.id.collectAsStateWithLifecycle()
    val password by signUpViewModel.password.collectAsStateWithLifecycle()
    val repeatPassword by signUpViewModel.repeatPassword.collectAsStateWithLifecycle()

    SignUpScreen(
        signUp = {SignUp(id, password, repeatPassword)},
        valids = signUpViewModel.valids,
        updateAccountEvent = signUpViewModel::updateAccountEvent,
        navigateToSignUpDetail = navigateToSignUpDetail
    )
}

@Composable
private fun SignUpScreen(
    signUp: () -> SignUp,
    valids : ValidValue,
    updateAccountEvent : (AccountEvent,String) -> Unit,
    navigateToSignUpDetail : (String,String) -> Unit
){
    DefaultLayout(
        contentPaddingValues = PaddingValues(vertical = 60.dp)
    ) {
        Text(
            text = stringResource(id = AppText.signup_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.fillMaxWidth().padding(start = 12.dp)
        )
        VerticalSpacer(height = 30.dp)

        IdDetail(
            id = {signUp().id},
            idValid = {valids.idValid.value},
            onNewIdValue = {
                val text = it.trim()
                if (text.length <= MAX_ID_LENGTH)
                    updateAccountEvent(AccountEvent.id,text)
            }
        )

        PasswordDetail(
            password = { signUp().password},
            repeatPassword = { signUp().repeatPassword},
            passwordValid = { valids.passwordValid.value },
            repeatPasswordValid = { valids.repeatPasswordValid.value },
            onNewPassword = {
                val text = it.trim()
                if (text.length <= MAX_PASS_LENGTH)
                    updateAccountEvent(AccountEvent.password,text)
            },
            onNewRepeatPassword = {
                val text = it.trim()
                if (text.length <= MAX_PASS_LENGTH)
                    updateAccountEvent(AccountEvent.repeatPassword,text)
            }
        )

        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxSize()
        ) {
            EnableButton(
                text = "다음 단계",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(50.dp),
                enabled = valids.isSuccessfulValid()
            ) {
                navigateToSignUpDetail(signUp().id,signUp().password)
            }
        }
    }
}

@Composable
@Preview
private fun PreviewSignUpScreen(

) = StepWalkTheme {
    SignUpScreen(
        signUp = {SignUp()},
        valids = ValidValue(SignValid.success,SignValid.notValid,SignValid.success),
        updateAccountEvent = {_,_ ->},
        navigateToSignUpDetail = {_,_ ->}
    )
}