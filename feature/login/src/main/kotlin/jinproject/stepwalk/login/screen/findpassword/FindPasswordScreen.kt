package jinproject.stepwalk.login.screen.findpassword

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.layout.DefaultLayout
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.component.EmailVerificationField
import jinproject.stepwalk.login.component.EnableButton
import jinproject.stepwalk.login.component.IdDetail
import jinproject.stepwalk.login.component.PasswordDetail
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.utils.MAX_EMAIL_CODE_LENGTH
import jinproject.stepwalk.login.utils.MAX_EMAIL_LENGTH
import jinproject.stepwalk.login.utils.MAX_ID_LENGTH
import jinproject.stepwalk.login.utils.MAX_PASS_LENGTH

@Composable
internal fun FindPasswordScreen(
    findPasswordViewModel: FindPasswordViewModel = hiltViewModel(),
    popBackStack :() -> Unit
) {

    FindPasswordScreen(
        id = findPasswordViewModel.id,
        password = findPasswordViewModel.password,
        repeatPassword = findPasswordViewModel.repeatPassword,
        email = findPasswordViewModel.email,
        emailCode = findPasswordViewModel.emailCode,
        nextStep = findPasswordViewModel.nextStep.value,
        updateFindEvent = findPasswordViewModel::updateFindEvent,
        requestEmailVerification = findPasswordViewModel::requestEmailVerification,
        requestFindAccount = findPasswordViewModel::requestFindAccount,
        requestResetPassword = findPasswordViewModel::requestResetPassword,
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
    updateFindEvent : (FindEvent,String) -> Unit,
    requestEmailVerification : () -> Unit,
    requestFindAccount: () -> Unit,
    requestResetPassword : () -> Unit,
    popBackStack: () -> Unit
){
    val scrollState = rememberScrollState()
    val idValue by id.value.collectAsStateWithLifecycle()
    val passwordValue by password.value.collectAsStateWithLifecycle()
    val repeatPasswordValue by repeatPassword.value.collectAsStateWithLifecycle()
    val emailValue by email.value.collectAsStateWithLifecycle()
    val emailCodeValue by emailCode.value.collectAsStateWithLifecycle()

    DefaultLayout(
        contentPaddingValues = PaddingValues(vertical = 60.dp)
    ) {
        Column(
            modifier = Modifier
                .imePadding()
                .verticalScroll(scrollState)
        ) {
            Image(
                imageVector = ImageVector.vectorResource(R.drawable.ic_fire),
                contentDescription = "캐릭터?or 운동이미지?",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .padding(horizontal = 12.dp, vertical = 30.dp),
                alignment = Alignment.Center
            )
            VerticalSpacer(height = 30.dp)
            if (nextStep){
                Text(
                    text = "재설정할 비밀번호를 입력해주세요.",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp)
                )
                VerticalSpacer(height = 30.dp)
                PasswordDetail(
                    password = passwordValue,
                    repeatPassword = repeatPasswordValue,
                    passwordValid = password.valid,
                    repeatPasswordValid = repeatPassword.valid,
                    onNewPassword = {
                        val text = it.trim()
                        if (text.length <= MAX_PASS_LENGTH)
                            updateFindEvent(FindEvent.password,text)
                    },
                    onNewRepeatPassword = {
                        val text = it.trim()
                        if (text.length <= MAX_PASS_LENGTH)
                            updateFindEvent(FindEvent.repeatPassword,text)
                    }
                )
                VerticalSpacer(height = 20.dp)
                EnableButton(
                    text = "비밀번호 재설정",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .height(50.dp),
                    enabled = password.isSuccessful() && repeatPassword.isSuccessful()
                ) {
                    requestResetPassword()
                    popBackStack()
                }
            }else{
                IdDetail(
                    id = idValue,
                    idValid = id.valid ,
                    onNewIdValue = {
                        val text = it.trim()
                        if (text.length <= MAX_ID_LENGTH)
                            updateFindEvent(FindEvent.id,text)
                    }
                )
                EmailVerificationField(
                    email = emailValue,
                    emailCode = emailCodeValue,
                    emailValid = email.valid,
                    emailCodeValid = emailCode.valid,
                    requestEmailVerification = requestEmailVerification,
                    onEmailValue = {
                        val text = it.trim()
                        if (text.length <= MAX_EMAIL_LENGTH)
                            updateFindEvent(FindEvent.email,it)
                    },
                    onVerificationCodeValue = {
                        val text = it.trim()
                        if (text.length <= MAX_EMAIL_CODE_LENGTH)
                            updateFindEvent(FindEvent.emailCode,it)
                    }
                )
                VerticalSpacer(height = 20.dp)
                EnableButton(
                    text = "비밀번호 재설정",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .height(50.dp),
                    enabled = email.isSuccessful() && emailCode.isSuccessful()
                ) {
                    requestFindAccount()
                }
            }
        }
    }
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
        updateFindEvent = {_,_ ->},
        requestEmailVerification = {  },
        requestFindAccount = {  },
        requestResetPassword = {  }) {
    }
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
        updateFindEvent = {_,_ ->},
        requestEmailVerification = {  },
        requestFindAccount = {  },
        requestResetPassword = {  }) {
    }
}