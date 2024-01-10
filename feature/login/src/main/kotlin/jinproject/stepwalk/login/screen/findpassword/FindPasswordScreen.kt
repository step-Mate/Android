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
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.component.EmailVerificationField
import jinproject.stepwalk.login.component.EnableButton
import jinproject.stepwalk.login.component.IdDetail
import jinproject.stepwalk.login.component.PasswordDetail
import jinproject.stepwalk.login.screen.state.SignValid
import jinproject.stepwalk.login.screen.state.AccountValid
import jinproject.stepwalk.login.screen.state.Verification
import jinproject.stepwalk.login.utils.MAX_ID_LENGTH
import jinproject.stepwalk.login.utils.MAX_PASS_LENGTH

@Composable
internal fun FindPasswordScreen(
    findPasswordViewModel: FindPasswordViewModel = hiltViewModel(),
    popBackStack :() -> Unit
) {
    val id by findPasswordViewModel.id.collectAsStateWithLifecycle()
    val email by findPasswordViewModel.email.collectAsStateWithLifecycle()
    val emailCode by findPasswordViewModel.emailCode.collectAsStateWithLifecycle()
    val password by findPasswordViewModel.password.collectAsStateWithLifecycle()
    val repeatPassword by findPasswordViewModel.repeatPassword.collectAsStateWithLifecycle()

    FindPasswordScreen(
        findAccountPassword = { FindAccountPassword(id, email, emailCode, password, repeatPassword,findPasswordViewModel.emailValid.value,findPasswordViewModel.nextStep.value) },
        valids = findPasswordViewModel.valids,
        updateFindEvent = findPasswordViewModel::updateFindEvent,
        requestEmailVerification = findPasswordViewModel::requestEmailVerification,
        requestFindAccount = findPasswordViewModel::requestFindAccount,
        requestResetPassword = findPasswordViewModel::requestResetPassword,
        popBackStack = popBackStack
    )
}

@Composable
private fun FindPasswordScreen(
    findAccountPassword: () -> FindAccountPassword,
    valids : AccountValid,
    updateFindEvent : (FindEvent,String) -> Unit,
    requestEmailVerification : () -> Unit,
    requestFindAccount: () -> Unit,
    requestResetPassword : () -> Unit,
    popBackStack: () -> Unit
){
    val scrollState = rememberScrollState()

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
            if (findAccountPassword().nextStep){
                Text(
                    text = "재설정할 비밀번호를 입력해주세요.",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.fillMaxWidth().padding(start = 12.dp)
                )
                VerticalSpacer(height = 30.dp)
                PasswordDetail(
                    password = { findAccountPassword().password},
                    repeatPassword = { findAccountPassword().repeatPassword},
                    passwordValid = { valids.passwordValid.value },
                    repeatPasswordValid = { valids.repeatPasswordValid.value },
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
                VerticalSpacer(height = 30.dp)
                EnableButton(
                    text = "비밀번호 재설정",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    enabled = (valids.passwordValid.value == SignValid.success) && (valids.repeatPasswordValid.value == SignValid.success)
                ) {
                    requestResetPassword()
                    popBackStack()
                }
            }else{
                IdDetail(
                    id = { findAccountPassword().id },
                    idValid = { valids.idValid.value },
                    onNewIdValue = {
                        val text = it.trim()
                        if (text.length <= MAX_ID_LENGTH)
                            updateFindEvent(FindEvent.id,text)
                    }
                )
                EmailVerificationField(
                    email = { findAccountPassword().email },
                    verificationCode = { findAccountPassword().emailCode },
                    isVerification = { findAccountPassword().emailValid },
                    requestEmailVerification = requestEmailVerification,
                    onEmailValue = { updateFindEvent(FindEvent.email,it) },
                    onVerificationCodeValue = { updateFindEvent(FindEvent.emailCode,it)}
                )
                VerticalSpacer(height = 30.dp)
                EnableButton(
                    text = "비밀번호 재설정",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp),
                    enabled = findAccountPassword().emailValid == Verification.success
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
        findAccountPassword = { FindAccountPassword(nextStep = true) },
        valids = AccountValid(),
        updateFindEvent = {_,_ ->},
        requestEmailVerification = {  },
        requestFindAccount = {  },
        requestResetPassword = {  }) {

    }
}