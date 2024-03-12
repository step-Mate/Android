package com.stepmate.login.screen.login

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.stepmate.core.SnackBarMessage
import com.stepmate.login.screen.component.FindAndSignUpButtons
import com.stepmate.core.MAX_ID_LENGTH
import com.stepmate.core.MAX_PASS_LENGTH
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.login.screen.component.EnableButton
import com.stepmate.login.screen.component.IdField
import com.stepmate.login.screen.component.LoginLayout
import com.stepmate.login.screen.component.PasswordField

@Composable
internal fun LoginScreen(
    loginViewModel: LoginViewModel = hiltViewModel(),
    navigateToInformation: () -> Unit,
    navigateToFindId: () -> Unit,
    navigateToFindPassword: () -> Unit,
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    val state by loginViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state) {
        if (state.isSuccess)
            popBackStack()
        else
            if (state.errorMessage.isNotEmpty() && !state.isLoading)
                showSnackBar(SnackBarMessage(state.errorMessage))
    }

    LoginScreen(
        checkValidAccount = loginViewModel::checkValidAccount,
        isLoading = state.isLoading,
        navigateToSignUp = navigateToInformation,
        navigateToFindId = navigateToFindId,
        navigateToFindPassword = navigateToFindPassword,
        popBackStack = popBackStack
    )
}

@Composable
private fun LoginScreen(
    checkValidAccount: (String, String) -> Unit,
    isLoading: Boolean,
    navigateToSignUp: () -> Unit,
    navigateToFindId: () -> Unit,
    navigateToFindPassword: () -> Unit,
    popBackStack: () -> Unit
) {
    var id by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("ic_run.json"))
    val lottieProgress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

    LoginLayout(
        text = "로그인",
        modifier = Modifier.padding(top = 10.dp),
        content = {
            LottieAnimation(
                composition = composition,
                progress = { lottieProgress },
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth()
                    .height(300.dp)
            )
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
                checkValidAccount(id, password)
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
) = StepMateTheme {
    LoginScreen(
        checkValidAccount = { _, _ -> },
        isLoading = false,
        navigateToSignUp = {},
        navigateToFindId = {},
        navigateToFindPassword = {},
        popBackStack = {}
    )
}