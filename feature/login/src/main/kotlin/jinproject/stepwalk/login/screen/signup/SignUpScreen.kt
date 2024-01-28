package jinproject.stepwalk.login.screen.signup

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.component.EnableButton
import jinproject.stepwalk.login.component.InformationField
import jinproject.stepwalk.login.component.LoginLayout
import jinproject.stepwalk.login.component.PasswordDetail
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.screen.state.SignValid
import jinproject.stepwalk.login.screen.state.isError
import jinproject.stepwalk.login.utils.MAX_ID_LENGTH
import jinproject.stepwalk.login.utils.MAX_PASS_LENGTH
import jinproject.stepwalk.design.R.string as AppText

@Composable
internal fun SignUpScreen(
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    navigateToSignUpDetail : (String,String) -> Unit,
    showSnackBar : (SnackBarMessage) -> Unit,
    popBackStack : () -> Unit,
) {
    val state by signUpViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state){
        if (state.isSuccess)
            navigateToSignUpDetail(signUpViewModel.id.now(),signUpViewModel.password.now())
        else
            if (state.errorMessage.isNotEmpty())
                showSnackBar(SnackBarMessage(state.errorMessage))
    }

    SignUpScreen(
        id = signUpViewModel.id,
        password = signUpViewModel.password,
        repeatPassword = signUpViewModel.repeatPassword,
        onEvent = signUpViewModel::onEvent,
        popBackStack = popBackStack
    )
}

@Composable
private fun SignUpScreen(
    id : Account,
    password : Account,
    repeatPassword : Account,
    onEvent : (SignUpEvent) -> Unit,
    popBackStack : () -> Unit,
){
    val idValue by id.value.collectAsStateWithLifecycle()
    val passwordValue by password.value.collectAsStateWithLifecycle()
    val repeatPasswordValue by repeatPassword.value.collectAsStateWithLifecycle()

    LoginLayout(
        text = "회원가입",
        modifier = Modifier.fillMaxSize(),
        content = {
            Text(
                text = stringResource(id = AppText.signup_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
            )
            VerticalSpacer(height = 30.dp)

            InformationField(
                informationText = "아이디",
                errorMessage = when (idValue.valid) {
                    SignValid.notValid -> "잘못된 아이디 양식이에요. 영어,숫자,_만 입력가능하고 4~12글자까지 입력가능해요."
                    SignValid.duplicationId -> "중복되는 아이디가 존재합니다."
                    SignValid.success -> "사용가능한 아이디 입니다."
                    else -> "아이디를 입력해주세요."
                },
                value = idValue.text,
                isError = idValue.valid.isError() || idValue.valid == SignValid.success,
                errorColor = if (idValue.valid.isError()) MaterialTheme.colorScheme.error else StepWalkColor.success.color,
                keyboardType = KeyboardType.Email,
                leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Email") },
                onNewValue = {
                    val text = it.trim()
                    if (text.length <= MAX_ID_LENGTH)
                        onEvent(SignUpEvent.Id(text))
                }
            )
            PasswordDetail(
                password = passwordValue.text,
                repeatPassword = repeatPasswordValue.text,
                passwordValid = passwordValue.valid ,
                repeatPasswordValid = repeatPasswordValue.valid ,
                onNewPassword = {
                    val text = it.trim()
                    if (text.length <= MAX_PASS_LENGTH)
                        onEvent(SignUpEvent.Password(text))
                },
                onNewRepeatPassword = {
                    val text = it.trim()
                    if (text.length <= MAX_PASS_LENGTH)
                        onEvent(SignUpEvent.RepeatPassword(text))
                }
            )
        },
        bottomContent = {
            EnableButton(
                text = "다음 단계",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = id.isSuccessful() && password.isSuccessful() && repeatPassword.isSuccessful()
            ) {
                onEvent(SignUpEvent.NextStep)
            }
        },
        popBackStack = popBackStack
    )
}

@Composable
@Preview
private fun PreviewSignUpScreen(
    @PreviewParameter(SignUpStatePreviewParameters::class)
    state : Account,
) = StepWalkTheme {
    SignUpScreen(
        id = state,
        password = state,
        repeatPassword = state,
        onEvent = {},
        popBackStack = {}
    )
}