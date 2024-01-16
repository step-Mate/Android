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
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.utils.MAX_ID_LENGTH
import jinproject.stepwalk.login.utils.MAX_PASS_LENGTH
import jinproject.stepwalk.login.utils.SnackBarMessage
import jinproject.stepwalk.design.R.string as AppText

@Composable
internal fun SignUpScreen(
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    navigateToSignUpDetail : (String,String) -> Unit,
    showSnackBar : (SnackBarMessage) -> Unit
) {

    SignUpScreen(
        id = signUpViewModel.id,
        password = signUpViewModel.password,
        repeatPassword = signUpViewModel.repeatPassword,
        updateAccountEvent = signUpViewModel::updateAccountEvent,
        checkAccountValid = signUpViewModel::checkAccountValid,
        navigateToSignUpDetail = navigateToSignUpDetail,
        showSnackBar = showSnackBar
    )
}

@Composable
private fun SignUpScreen(
    id : Account,
    password : Account,
    repeatPassword : Account,
    updateAccountEvent : (AccountEvent,String) -> Unit,
    checkAccountValid : () -> Boolean,
    navigateToSignUpDetail : (String,String) -> Unit,
    showSnackBar : (SnackBarMessage) -> Unit
){
    val idValue by id.value.collectAsStateWithLifecycle()
    val passwordValue by password.value.collectAsStateWithLifecycle()
    val repeatPasswordValue by repeatPassword.value.collectAsStateWithLifecycle()

    DefaultLayout(
        contentPaddingValues = PaddingValues(vertical = 60.dp)
    ) {
        Text(
            text = stringResource(id = AppText.signup_title),
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 12.dp)
        )
        VerticalSpacer(height = 30.dp)

        IdDetail(
            id = idValue,
            idValid = id.valid,
            onNewIdValue = {
                val text = it.trim()
                if (text.length <= MAX_ID_LENGTH)
                    updateAccountEvent(AccountEvent.id,text)
            }
        )

        PasswordDetail(
            password = passwordValue,
            repeatPassword = repeatPasswordValue,
            passwordValid = password.valid ,
            repeatPasswordValid = repeatPassword.valid ,
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
                enabled = id.isSuccessful() && password.isSuccessful() && repeatPassword.isSuccessful()
            ) {
                if (checkAccountValid())
                    navigateToSignUpDetail(idValue,passwordValue)
                else
                    showSnackBar(
                        SnackBarMessage(
                            headerMessage = "계정 정보를 다시 한번 확인해주세요."
                        )
                    )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewSignUpScreen(

) = StepWalkTheme {
    SignUpScreen(
        id = Account(500),
        password = Account(500),
        repeatPassword = Account(500),
        updateAccountEvent = {_,_ ->},
        checkAccountValid = {true},
        navigateToSignUpDetail = {_,_ ->},
        showSnackBar = {}
    )
}