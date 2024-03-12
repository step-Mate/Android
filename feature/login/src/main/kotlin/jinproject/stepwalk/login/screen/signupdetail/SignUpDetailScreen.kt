package jinproject.stepwalk.login.screen.signupdetail

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.core.MAX_EMAIL_CODE_LENGTH
import jinproject.stepwalk.core.MAX_EMAIL_LENGTH
import jinproject.stepwalk.core.MAX_NICKNAME_LENGTH
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.design.component.DefaultOutlinedTextField
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.screen.component.EmailVerificationField
import jinproject.stepwalk.login.screen.component.EnableButton
import jinproject.stepwalk.login.screen.component.LoginLayout
import jinproject.stepwalk.login.screen.signup.SignUpStatePreviewParameters
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.screen.state.SignValid
import jinproject.stepwalk.login.screen.state.isError
import jinproject.stepwalk.login.screen.state.isSuccess

@Composable
internal fun SignUpDetailScreen(
    signUpDetailViewModel: SignUpDetailViewModel = hiltViewModel(),
    popBackStack: () -> Unit,
    backStackToLogin: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    val state by signUpDetailViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state) {
        if (state.isSuccess) {
            backStackToLogin()
        } else {
            if (state.errorMessage.isNotEmpty() && !state.isLoading) {
                showSnackBar(SnackBarMessage(state.errorMessage))
            }
        }
    }

    SignUpDetailScreen(
        nickname = signUpDetailViewModel.nickname,
        email = signUpDetailViewModel.email,
        emailCode = signUpDetailViewModel.emailCode,
        isLoading = state.isLoading,
        onEvent = signUpDetailViewModel::onEvent,
        popBackStack = popBackStack
    )
}

@Composable
private fun SignUpDetailScreen(
    nickname: Account,
    email: Account,
    emailCode: Account,
    isLoading: Boolean,
    onEvent: (SignUpDetailEvent) -> Unit,
    popBackStack: () -> Unit,
) {
    val nicknameValue by nickname.value.collectAsStateWithLifecycle()
    val emailCodeValue by emailCode.value.collectAsStateWithLifecycle()

    LoginLayout(
        text = "회원가입",
        modifier = Modifier.padding(top = 30.dp),
        content = {
            VerticalSpacer(height = 80.dp)
            DefaultOutlinedTextField(
                informationText = "닉네임",
                errorMessage = if (nicknameValue.valid == SignValid.duplicationId) "중복된 닉네임이 존재합니다."
                else "한글,영어,숫자가능,특수문자불가,2~10글자까지 입력가능",
                value = nicknameValue.text,
                isError = nicknameValue.valid.isError()
            ) {
                val text = it.trim()
                if (text.length <= MAX_NICKNAME_LENGTH)
                    onEvent(SignUpDetailEvent.Nickname(text))
            }

            EmailVerificationField(
                email = email,
                emailCode = emailCode,
                requestEmailVerification = { onEvent(SignUpDetailEvent.RequestEmail) },
                onEmailValue = {
                    val text = it.trim()
                    if (text.length <= MAX_EMAIL_LENGTH)
                        onEvent(SignUpDetailEvent.Email(text))
                },
                onVerificationCodeValue = {
                    val text = it.trim()
                    if (text.length <= MAX_EMAIL_CODE_LENGTH)
                        onEvent(SignUpDetailEvent.EmailCode(text))
                }
            )
        },
        bottomContent = {
            EnableButton(
                text = "계정 생성",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = nicknameValue.valid.isSuccess() && emailCodeValue.valid.isSuccess() && !isLoading,
                loading = isLoading
            ) {
                onEvent(SignUpDetailEvent.SignUp)
            }
        },
        popBackStack = popBackStack
    )
}

@Composable
@Preview
private fun PreviewSignUpDetailScreen(
    @PreviewParameter(SignUpStatePreviewParameters::class)
    state: Account
) = StepWalkTheme {
    SignUpDetailScreen(
        nickname = state,
        email = state,
        emailCode = state,
        isLoading = false,
        onEvent = {},
        popBackStack = {}
    )
}