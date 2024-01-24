package jinproject.stepwalk.login.screen.findpassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.component.EmailVerificationField
import jinproject.stepwalk.login.component.EnableButton
import jinproject.stepwalk.login.component.IdField
import jinproject.stepwalk.login.component.LoginLayout
import jinproject.stepwalk.login.component.PasswordDetail
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.utils.MAX_EMAIL_CODE_LENGTH
import jinproject.stepwalk.login.utils.MAX_EMAIL_LENGTH
import jinproject.stepwalk.login.utils.MAX_ID_LENGTH
import jinproject.stepwalk.login.utils.MAX_PASS_LENGTH

@Composable
internal fun FindPasswordScreen(
    findPasswordViewModel: FindPasswordViewModel = hiltViewModel(),
    popBackStack :() -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    val state by findPasswordViewModel.state.collectAsStateWithLifecycle()
    val nextStep by findPasswordViewModel.nextStep.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state){
        if (state.isSuccess){
            popBackStack()
        }else{
            if (state.errorMessage.isNotEmpty())
                showSnackBar(SnackBarMessage(state.errorMessage))
        }
    }


    FindPasswordScreen(
        id = findPasswordViewModel.id,
        password = findPasswordViewModel.password,
        repeatPassword = findPasswordViewModel.repeatPassword,
        email = findPasswordViewModel.email,
        emailCode = findPasswordViewModel.emailCode,
        nextStep = nextStep,
        onEvent = findPasswordViewModel::onEvent,
        popBackStack = popBackStack
    )
}

@Composable
private fun FindPasswordScreen(
    id : Account,
    password : Account,
    repeatPassword : Account,
    email : Account,
    emailCode : Account,
    nextStep : Boolean,
    onEvent : (FindPasswordEvent) -> Unit,
    popBackStack: () -> Unit
){
    val idValue by id.value.collectAsStateWithLifecycle()
    val passwordValue by password.value.collectAsStateWithLifecycle()
    val repeatPasswordValue by repeatPassword.value.collectAsStateWithLifecycle()
    val emailValue by email.value.collectAsStateWithLifecycle()
    val emailCodeValue by emailCode.value.collectAsStateWithLifecycle()

    val idValid by id.valid.collectAsStateWithLifecycle()
    val passwordValid by password.valid.collectAsStateWithLifecycle()
    val repeatPasswordValid by repeatPassword.valid.collectAsStateWithLifecycle()
    val emailValid by email.valid.collectAsStateWithLifecycle()
    val emailCodeValid by emailCode.valid.collectAsStateWithLifecycle()


    LoginLayout(
        text = "회원가입",
        modifier = Modifier.padding(top = 20.dp),
        content = {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_fire),
                contentDescription = "캐릭터?or 운동이미지?",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(vertical = 30.dp),
                alignment = Alignment.Center
            )
            VerticalSpacer(height = 30.dp)
            if (nextStep){
                Text(
                    text = "재설정할 비밀번호를 입력해주세요.",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                VerticalSpacer(height = 30.dp)
                PasswordDetail(
                    password = passwordValue,
                    repeatPassword = repeatPasswordValue,
                    passwordValid = passwordValid,
                    repeatPasswordValid = repeatPasswordValid,
                    onNewPassword = {
                        val text = it.trim()
                        if (text.length <= MAX_PASS_LENGTH)
                            onEvent(FindPasswordEvent.Password(text))
                    },
                    onNewRepeatPassword = {
                        val text = it.trim()
                        if (text.length <= MAX_PASS_LENGTH)
                            onEvent(FindPasswordEvent.RepeatPassword(text))
                    }
                )
            }else {
                IdField(
                    value = idValue,
                    onNewValue = {
                        val text = it.trim()
                        if (text.length <= MAX_ID_LENGTH)
                            onEvent(FindPasswordEvent.Id(text))
                    },
                    idValid = idValid
                )
                EmailVerificationField(
                    email = emailValue,
                    emailCode = emailCodeValue,
                    emailValid = emailValid,
                    emailCodeValid = emailCodeValid,
                    requestEmailVerification = { onEvent(FindPasswordEvent.RequestEmail) },
                    onEmailValue = {
                        val text = it.trim()
                        if (text.length <= MAX_EMAIL_LENGTH)
                            onEvent(FindPasswordEvent.Email(text))
                    },
                    onVerificationCodeValue = {
                        val text = it.trim()
                        if (text.length <= MAX_EMAIL_CODE_LENGTH)
                            onEvent(FindPasswordEvent.EmailCode(text))
                    }
                )
            }
        },
        bottomContent = {
            EnableButton(
                text = "비밀번호 재설정",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .height(50.dp),
                enabled = if(nextStep) password.isSuccessful() && repeatPassword.isSuccessful() else email.isSuccessful() && emailCode.isSuccessful()
            ) {
                if (nextStep){
                    onEvent(FindPasswordEvent.ResetPassword)
                }else{
                    onEvent(FindPasswordEvent.CheckVerification)
                }

            }
        },
        popBackStack = popBackStack
    )
}

@Composable
@Preview
private fun PreviewFindPasswordScreen(

) = StepWalkTheme {
    FindPasswordScreen(
        id = Account(500),
        password = Account(500),
        repeatPassword = Account(500),
        email= Account(500),
        emailCode = Account(500),
        nextStep = false,
        onEvent = {},
        popBackStack = {}
    )
}

@Composable
@Preview
private fun PreviewFindPasswordScreen2(

) = StepWalkTheme {
    FindPasswordScreen(
        id = Account(500),
        password = Account(500),
        repeatPassword = Account(500),
        email= Account(500),
        emailCode = Account(500),
        nextStep = true,
        onEvent = {},
        popBackStack = {}
    )
}