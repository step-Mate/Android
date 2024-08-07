package com.stepmate.login.screen.findpassword

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stepmate.core.SnackBarMessage
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.login.screen.component.EmailVerificationField
import com.stepmate.login.screen.component.EnableButton
import com.stepmate.login.screen.component.IdField
import com.stepmate.login.screen.component.LoginLayout
import com.stepmate.login.screen.component.PasswordDetail
import com.stepmate.login.screen.state.Account
import com.stepmate.login.screen.state.isSuccess
import com.stepmate.core.MAX_EMAIL_CODE_LENGTH
import com.stepmate.core.MAX_EMAIL_LENGTH
import com.stepmate.core.MAX_ID_LENGTH
import com.stepmate.core.MAX_PASS_LENGTH

@Composable
internal fun FindPasswordScreen(
    findPasswordViewModel: FindPasswordViewModel = hiltViewModel(),
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    val state by findPasswordViewModel.state.collectAsStateWithLifecycle()
    val nextStep by findPasswordViewModel.nextStep.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state) {
        if (state.isSuccess) {
            popBackStack()
        } else {
            if (state.errorMessage.isNotEmpty() && !state.isLoading)
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
        isLoading = state.isLoading,
        onEvent = findPasswordViewModel::onEvent,
        popBackStack = popBackStack
    )
}

@Composable
private fun FindPasswordScreen(
    id: Account,
    password: Account,
    repeatPassword: Account,
    email: Account,
    emailCode: Account,
    nextStep: Boolean,
    isLoading: Boolean,
    onEvent: (FindPasswordEvent) -> Unit,
    popBackStack: () -> Unit
) {
    val idValue by id.value.collectAsStateWithLifecycle()
    val passwordValue by password.value.collectAsStateWithLifecycle()
    val repeatPasswordValue by repeatPassword.value.collectAsStateWithLifecycle()
    val emailCodeValue by emailCode.value.collectAsStateWithLifecycle()

    LoginLayout(
        text = "비밀번호 찾기",
        modifier = Modifier.padding(top = 20.dp),
        content = {
            VerticalSpacer(height = 100.dp)
            if (nextStep) {
                Text(
                    text = "재설정할 비밀번호를 입력해주세요.",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                )
                VerticalSpacer(height = 30.dp)
                PasswordDetail(
                    password = passwordValue.text,
                    repeatPassword = repeatPasswordValue.text,
                    passwordValid = passwordValue.valid,
                    repeatPasswordValid = repeatPasswordValue.valid,
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
            } else {
                IdField(
                    value = idValue.text,
                    onNewValue = {
                        val text = it.trim()
                        if (text.length <= MAX_ID_LENGTH)
                            onEvent(FindPasswordEvent.Id(text))
                    },
                    idValid = idValue.valid
                )
                EmailVerificationField(
                    email = email,
                    emailCode = emailCode,
                    codeVisibility = true,
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
            if (nextStep) {
                EnableButton(
                    text = "비밀번호 변경",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .height(50.dp),
                    enabled = passwordValue.valid.isSuccess() && repeatPasswordValue.valid.isSuccess(),
                    loading = isLoading,
                    onClick = { onEvent(FindPasswordEvent.ResetPassword) },
                )
            } else {
                EnableButton(
                    text = "비밀번호 재설정",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .height(50.dp),
                    enabled = emailCodeValue.valid.isSuccess() && !isLoading,
                    loading = isLoading,
                    onClick = { onEvent(FindPasswordEvent.CheckVerification) },
                )
            }
        },
        popBackStack = popBackStack
    )
}

@Composable
@Preview
private fun PreviewFindPasswordScreen(

) = StepMateTheme {
    FindPasswordScreen(
        id = Account(500),
        password = Account(500),
        repeatPassword = Account(500),
        email = Account(500),
        emailCode = Account(500),
        nextStep = false,
        isLoading = false,
        onEvent = {},
        popBackStack = {}
    )
}

@Composable
@Preview
private fun PreviewFindPasswordScreen2(

) = StepMateTheme {
    FindPasswordScreen(
        id = Account(500),
        password = Account(500),
        repeatPassword = Account(500),
        email = Account(500),
        emailCode = Account(500),
        nextStep = true,
        isLoading = false,
        onEvent = {},
        popBackStack = {}
    )
}