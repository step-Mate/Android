package jinproject.stepwalk.login.screen.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.component.EnableButton
import jinproject.stepwalk.login.component.IdField
import jinproject.stepwalk.login.component.LoginLayout
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
            if (state.errorMessage.isNotEmpty() && !state.isLoading)
                showSnackBar(SnackBarMessage(state.errorMessage))
    }

    LoginScreen(
        checkValidAccount = loginViewModel::checkValidAccount,
        isLoading = state.isLoading,
        navigateToSignUp = navigateToSignUp,
        navigateToFindId = navigateToFindId,
        navigateToFindPassword = navigateToFindPassword,
        popBackStack = popBackStack
    )
}

@Composable
private fun LoginScreen(
    checkValidAccount : (String,String) -> Unit,
    isLoading : Boolean,
    navigateToSignUp: () -> Unit,
    navigateToFindId : () -> Unit,
    navigateToFindPassword : () -> Unit,
    popBackStack : () -> Unit
) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    LoginLayout(
        text = "로그인",
        modifier = Modifier.padding(top = 10.dp),
        content = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 30.dp),
            ) {
                Image(
                    imageVector = ImageVector.vectorResource(R.drawable.ic_fire),
                    contentDescription = "캐릭터?or 운동이미지?",
                    modifier = Modifier.size(200.dp)
                )
            }
            VerticalSpacer(height = 20.dp)

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
        },
        bottomContent = {
            EnableButton(
                text = "로그인",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = !isLoading,
                loading = isLoading
            ) {
                checkValidAccount(id, password)//수정
            }
            FindAndSignUpButtons(
                findAccountId = navigateToFindId,
                findAccountPassword = navigateToFindPassword,
                createAccount = navigateToSignUp
            )
        },
        popBackStack = popBackStack
    )
}
@Composable
@Preview
private fun PreviewHomeScreen(
) = StepWalkTheme {
    LoginScreen(
        checkValidAccount = {_,_ ->},
        isLoading = false,
        navigateToSignUp = {},
        navigateToFindId = {},
        navigateToFindPassword = {},
        popBackStack = {}
    )
}