package jinproject.stepwalk.login.screen.findid

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
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
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.design.component.DefaultButton
import jinproject.stepwalk.design.component.layout.DefaultLayout
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.screen.component.EmailVerificationField
import jinproject.stepwalk.login.screen.component.EnableButton
import jinproject.stepwalk.login.screen.component.IdResultDetail
import jinproject.stepwalk.login.screen.component.LoginLayout
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.screen.state.isSuccess
import jinproject.stepwalk.login.utils.MAX_EMAIL_CODE_LENGTH
import jinproject.stepwalk.login.utils.MAX_EMAIL_LENGTH

@Composable
internal fun FindIdScreen(
    findIdViewModel: FindIdViewModel = hiltViewModel(),
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    val state by findIdViewModel.state.collectAsStateWithLifecycle()
    val id by findIdViewModel.id.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state.errorMessage) {
        if (state.errorMessage.isNotEmpty())
            showSnackBar(SnackBarMessage(state.errorMessage))
    }

    FindIdScreen(
        id = id,
        email = findIdViewModel.email,
        emailCode = findIdViewModel.emailCode,
        nextStep = state.isSuccess,
        isLoading = state.isLoading,
        onEvent = findIdViewModel::onEvent,
        popBackStack = popBackStack
    )
}

@Composable
private fun FindIdScreen(
    id: String,
    email: Account,
    emailCode: Account,
    nextStep: Boolean,
    isLoading: Boolean,
    onEvent: (FindIdEvent) -> Unit,
    popBackStack: () -> Unit
) {
    val emailCodeValue by emailCode.value.collectAsStateWithLifecycle()

    LoginLayout(
        text = "아이디 찾기",
        modifier = Modifier.padding(top = 20.dp),
        content = {
            VerticalSpacer(height = 150.dp)
            if (nextStep) {//조회후 화면
                IdResultDetail(findAccountId = id)
                DefaultButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp)
                        .height(50.dp),
                    onClick = popBackStack,
                    shape = RoundedCornerShape(5.dp)
                ) {
                    Text(text = "확인", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                EmailVerificationField(
                    email = email,
                    emailCode = emailCode,
                    requestEmailVerification = { onEvent(FindIdEvent.RequestEmail) },
                    onEmailValue = {
                        val text = it.trim()
                        if (text.length <= MAX_EMAIL_LENGTH)
                            onEvent(FindIdEvent.Email(text))
                    },
                    onVerificationCodeValue = {
                        val text = it.trim()
                        if (text.length <= MAX_EMAIL_CODE_LENGTH)
                            onEvent(FindIdEvent.EmailCode(text))
                    }
                )
            }
        },
        bottomContent = {
            if (!nextStep) {
                EnableButton(
                    text = "아이디 찾기",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .height(50.dp),
                    enabled = emailCodeValue.valid.isSuccess() && !isLoading,
                    loading = isLoading
                ) {
                    onEvent(FindIdEvent.FindId)
                }
            }
        },
        popBackStack = popBackStack
    )
}

@Composable
@Preview
private fun PreviewFindAccountScreen2(

) = StepWalkTheme {
    FindIdScreen(
        id = "",
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
private fun PreviewFindAccountScreen(

) = StepWalkTheme {
    FindIdScreen(
        id = "",
        email = Account(500),
        emailCode = Account(500),
        nextStep = true,
        isLoading = true,
        onEvent = {},
        popBackStack = {}
    )
}