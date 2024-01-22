package jinproject.stepwalk.login.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.login.component.FindAndSignUpButtons
import jinproject.stepwalk.login.utils.MAX_ID_LENGTH
import jinproject.stepwalk.login.utils.MAX_PASS_LENGTH
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.component.EnableButton
import jinproject.stepwalk.login.component.IdField
import jinproject.stepwalk.login.component.PasswordField

@Composable
internal fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    navigateToSignUp : () -> Unit,
    navigateToFindId : () -> Unit,
    navigateToFindPassword : () -> Unit,
    popBackStack : () -> Unit,
    showSnackBar : (SnackBarMessage) -> Unit
){
    val state by loginViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state){
        if (state.isSuccess)
            popBackStack()
        else
            if (state.errorMessage.isNotEmpty())
                showSnackBar(SnackBarMessage(state.errorMessage))
    }

    LoginScreen(
        checkValidAccount = loginViewModel::checkValidAccount,
        navigateToSignUp = navigateToSignUp,
        navigateToFindId = navigateToFindId,
        navigateToFindPassword = navigateToFindPassword
    )
}

@Composable
private fun LoginScreen(
    checkValidAccount : (String,String) -> Unit,
    navigateToSignUp: () -> Unit,
    navigateToFindId : () -> Unit,
    navigateToFindPassword : () -> Unit
) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()

    DefaultLayout(
        modifier = Modifier,
        contentPaddingValues = PaddingValues(vertical = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .imePadding()
                .verticalScroll(scrollState)
        ) {
            //이미지? or 캐릭터? 수정예정
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(horizontal = 12.dp, vertical = 30.dp),
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_fire),
                    contentDescription = "캐릭터?or 운동이미지?",
                    modifier = Modifier.size(200.dp)
                )
            }
            VerticalSpacer(height = 40.dp)

            IdField(
                value = id,
                onNewValue = {
                    if (it.length <= MAX_ID_LENGTH)
                        id = it
                }
            )
            PasswordField(
                informationText = "비밀번호",
                value = password,
                onNewValue = {
                    if (it.length <= MAX_PASS_LENGTH)
                        password = it
                }
            )
            VerticalSpacer(height = 20.dp)

            EnableButton(
                text = "로그인",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(50.dp),
                enabled = true
            ) {
                checkValidAccount(id, password)
            }

            FindAndSignUpButtons(
                findAccountId = navigateToFindId,
                findAccountPassword = navigateToFindPassword,
                createAccount = navigateToSignUp
            )


//        VerticalSpacer(height = 10.dp)
//        Row (
//            verticalAlignment = Alignment.CenterVertically,
//            horizontalArrangement = Arrangement.SpaceBetween,
//            modifier = Modifier
//                .fillMaxWidth()
//                .padding(horizontal = 12.dp, vertical = 6.dp)
//        ){
//            GrayHorizontalDivider(modifier = Modifier.weight(0.4f))
//            Text(
//                text = "간편 로그인",
//                style = MaterialTheme.typography.bodySmall,
//                color = MaterialTheme.colorScheme.scrim,
//                modifier = Modifier.padding(horizontal = 10.dp)
//            )
//            GrayHorizontalDivider(modifier = Modifier.weight(0.4f))
//        }
//
//        VerticalSpacer(height = 20.dp)
//
//        IconButton(
//            icon = R.drawable.ic_kakao_simbol,
//            containerColor = StepWalkColor.kakao_yellow.color,
//            simbolColor = StepWalkColor.kakao_black.color,
//            labelColor = StepWalkColor.kakao_black.color,
//            text = AppText.kakao_login_button
//        ) {
//            //카카오 로그인 or 회원가입 처리
//        }
        }
    }
}
@Composable
@Preview
private fun PreviewHomeScreen(
) = StepWalkTheme {
    LoginScreen(
        checkValidAccount = {_,_ ->},
        navigateToSignUp = {},
        navigateToFindId = {},
        navigateToFindPassword = {}
    )
}