package jinproject.stepwalk.login.screen.findid

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
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
import jinproject.stepwalk.design.component.DefaultButton
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.component.EmailVerificationField
import jinproject.stepwalk.login.component.EnableButton
import jinproject.stepwalk.login.component.IdResultDetail
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.utils.MAX_EMAIL_CODE_LENGTH
import jinproject.stepwalk.login.utils.MAX_EMAIL_LENGTH

@Composable
internal fun FindIdScreen(
    findIdViewModel: FindIdViewModel = hiltViewModel(),
    popBackStack : () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
){
    val state by findIdViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state.errorMessage){
        if (state.errorMessage.isNotEmpty())
            showSnackBar(SnackBarMessage(state.errorMessage))
    }

    FindIdScreen(
        id = findIdViewModel.id,
        email = findIdViewModel.email,
        emailCode = findIdViewModel.emailCode,
        nextStep = state.isSuccess,
        onEvent = findIdViewModel::onEvent,
        popBackStack = popBackStack
    )
}

@Composable
private fun FindIdScreen(
    id : String,
    email : Account,
    emailCode : Account,
    nextStep : Boolean,
    onEvent: (FindIdEvent) -> Unit,
    popBackStack: () -> Unit
){
    val scrollState = rememberScrollState()
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
            if (nextStep) {//조회후 화면
                IdResultDetail(findAccountId = id)
                DefaultButton(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .height(50.dp),
                    onClick = popBackStack,
                    shape = RoundedCornerShape(5.dp)
                ) {
                   Text(text = "확인", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                EmailVerificationField(
                    email = emailValue,
                    emailCode = emailCodeValue,
                    emailValid = email.valid,
                    emailCodeValid = emailCode.valid,
                    requestEmailVerification = {onEvent(FindIdEvent.requestEmail)},
                    onEmailValue = {
                        val text = it.trim()
                        if (text.length <= MAX_EMAIL_LENGTH)
                            onEvent(FindIdEvent.email(text))
                    },
                    onVerificationCodeValue = {
                        val text = it.trim()
                        if (text.length <= MAX_EMAIL_CODE_LENGTH)
                            onEvent(FindIdEvent.emailCode(text))
                    }
                )
                VerticalSpacer(height = 20.dp)
                EnableButton(
                    text = "아이디 찾기",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .height(50.dp),
                    enabled = email.isSuccessful() && emailCode.isSuccessful()
                ) {
                    onEvent(FindIdEvent.findId)
                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewFindAccountScreen2(

) = StepWalkTheme {
    FindIdScreen(
        id = "",
        email= Account(500),
        emailCode = Account(500),
        nextStep = false,
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
        email= Account(500),
        emailCode = Account(500),
        nextStep = true,
        onEvent = {},
        popBackStack = {}
    )
}