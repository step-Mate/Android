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
import jinproject.stepwalk.design.component.DefaultButton
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.component.EmailVerificationField
import jinproject.stepwalk.login.component.EnableButton
import jinproject.stepwalk.login.component.IdResultDetail
import jinproject.stepwalk.login.screen.state.Verification

@Composable
internal fun FindIdScreen(
    findIdViewModel: FindIdViewModel = hiltViewModel(),
    popBackStack : () -> Unit
){
    val email by findIdViewModel.email.collectAsStateWithLifecycle()
    val emailCode by findIdViewModel.emailCode.collectAsStateWithLifecycle()

    FindIdScreen(
        findAccountId = { FindAccountId(email,emailCode,findIdViewModel.emailValid.value,findIdViewModel.nextStep.value,findIdViewModel.id.value) },
        updateEmail = findIdViewModel::updateEmail,
        updateEmailCode = findIdViewModel::updateEmailCode,
        requestEmailVerification = findIdViewModel::requestEmailVerification,
        requestFindAccount = findIdViewModel::requestFindAccount,
        popBackStack = popBackStack
    )
}

@Composable
private fun FindIdScreen(
    findAccountId : () -> FindAccountId,
    updateEmail : (String) -> Unit,
    updateEmailCode : (String) -> Unit,
    requestEmailVerification : () -> Unit,
    requestFindAccount: () -> Unit,
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
            if (findAccountId().nextStep) {//조회후 화면
                IdResultDetail(findAccountId = {findAccountId().id})
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
                    email = { findAccountId().email },
                    verificationCode = { findAccountId().emailCode },
                    isVerification = { findAccountId().emailValid },
                    requestEmailVerification = requestEmailVerification,
                    onEmailValue = updateEmail,
                    onVerificationCodeValue = updateEmailCode
                )
                VerticalSpacer(height = 20.dp)
                EnableButton(
                    text = "아이디 찾기",
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                        .height(50.dp),
                    enabled = findAccountId().emailValid == Verification.success
                ) {
                    requestFindAccount()
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
        findAccountId = { FindAccountId(nextStep = true, id = "Testttttt") },
        updateEmail = {},
        updateEmailCode = {},
        requestEmailVerification = {},
        requestFindAccount = {},
        popBackStack = {}
    )
}

@Composable
@Preview
private fun PreviewFindAccountScreen(

) = StepWalkTheme {
    FindIdScreen(
        findAccountId = { FindAccountId(nextStep = false, id = "Testttttt") },
        updateEmail = {},
        updateEmailCode = {},
        requestEmailVerification = {},
        requestFindAccount = {},
        popBackStack = {}
    )
}