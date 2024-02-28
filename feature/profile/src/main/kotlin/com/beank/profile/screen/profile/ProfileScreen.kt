package com.beank.profile.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.beank.profile.screen.profile.ProfileViewModel.Companion.CANNOT_LOGIN_EXCEPTION
import com.beank.profile.screen.profile.component.PasswordDialog
import com.beank.profile.screen.profile.component.ProfileButton
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.DialogState
import jinproject.stepwalk.design.component.HeadlineText
import jinproject.stepwalk.design.component.HorizontalDivider
import jinproject.stepwalk.design.component.StepMateDialog
import jinproject.stepwalk.design.component.StepMateProgressIndicatorRotating
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navigateToEditUser: (String, String, Boolean) -> Unit,
    navigateToTerms: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle(initialValue = ProfileViewModel.UiState.Loading)

    LaunchedEffect(key1 = uiState) {
        if (uiState is ProfileViewModel.UiState.Error) {
            val exception = (uiState as ProfileViewModel.UiState.Error).exception
            if (exception == CANNOT_LOGIN_EXCEPTION && exception.message == CANNOT_LOGIN_EXCEPTION.message) {
                showSnackBar(
                    SnackBarMessage(
                        "로그인이 만료 되었습니다."
                    )
                )
            } else
                showSnackBar(
                    SnackBarMessage(
                        "계정 정보를 불러올수 없습니다."
                    )
                )
        }
    }

    when (uiState) {
        ProfileViewModel.UiState.Loading -> {
            StepMateProgressIndicatorRotating()
        }

        ProfileViewModel.UiState.Login -> {
            ProfileScreen(
                user = profileViewModel.user,
                passwordValid = profileViewModel.passwordValid,
                anonymous = false,
                onEvent = profileViewModel::onEvent,
                navigateToEditUser = navigateToEditUser,
                navigateToTerms = navigateToTerms
            )
        }

        ProfileViewModel.UiState.Anonymous, is ProfileViewModel.UiState.Error -> {
            ProfileScreen(
                user = profileViewModel.user,
                passwordValid = profileViewModel.passwordValid,
                anonymous = true,
                onEvent = profileViewModel::onEvent,
                navigateToEditUser = navigateToEditUser,
                navigateToTerms = navigateToTerms
            )
        }
    }
}

@Composable
private fun ProfileScreen(
    user: StateFlow<User>,
    passwordValid: StateFlow<PasswordValid>,
    anonymous: Boolean,
    onEvent: (ProfileEvent) -> Unit,
    navigateToEditUser: (String, String, Boolean) -> Unit,
    navigateToTerms: () -> Unit,
) {
    val userState by user.collectAsStateWithLifecycle()
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(userState.character))
    val lottieProgress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    val backgroundCircle = MaterialTheme.colorScheme.primaryContainer
    var logoutState by remember { mutableStateOf(false) }
    var withdrawalState by remember { mutableStateOf(false) }
    var passwordState by remember { mutableStateOf(false) }

    DefaultLayout(
        contentPaddingValues = PaddingValues()
    ) {
        VerticalSpacer(height = 130.dp)
        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .background(
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(8.dp)
                )
                .fillMaxWidth()
                .clickable {
                    navigateToEditUser(userState.name, userState.designation, anonymous)
                }
        ) {
            Column(
                modifier = Modifier
                    .padding(start = 30.dp, top = 50.dp, bottom = 20.dp)
                    .align(Alignment.CenterStart)
            ) {
                HeadlineText(
                    text = "${userState.name}  LV.${userState.level}",
                    color = MaterialTheme.colorScheme.primaryContainer
                )
                DescriptionLargeText(
                    modifier = Modifier.padding(top = 5.dp),
                    text = userState.designation,
                    color = MaterialTheme.colorScheme.primaryContainer
                )
            }
            LottieAnimation(
                composition = composition,
                progress = { lottieProgress },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-80).dp)
                    .size(120.dp)
                    .drawBehind {
                        drawCircle(
                            color = backgroundCircle,
                            radius = 180.dp.value
                        )
                    }
            )
        }

        HorizontalDivider(
            modifier = Modifier.padding(top = 30.dp, start = 12.dp, end = 12.dp),
            thickness = 2.dp
        )
        VerticalSpacer(height = 10.dp)
        ProfileButton(
            text = "이용 약관",
            onClick = navigateToTerms
        )
        if (!anonymous) {
            ProfileButton(
                text = "로그 아웃",
                onClick = {
                    logoutState = true
                }
            )
            ProfileButton(
                text = "회원 탈퇴",
                onClick = {
                    withdrawalState = true
                }
            )
        }
        StepMateDialog(
            dialogState = DialogState(
                header = "로그아웃 하시겠습니까?",
                positiveMessage = "로그아웃",
                negativeMessage = "취소",
                onPositiveCallback = {
                    onEvent(ProfileEvent.Logout)
                    logoutState = false
                },
                onNegativeCallback = { logoutState = false },
                isShown = logoutState
            ),
            hideDialog = { logoutState = false }
        )
        StepMateDialog(
            dialogState = DialogState(
                header = "회원탈퇴 하시겠습니까?",
                content = "탈퇴 후 계정 복구는 불가합니다.",
                positiveMessage = "회원탈퇴",
                negativeMessage = "취소",
                onPositiveCallback = {
                    passwordState = true
                    withdrawalState = false
                },
                onNegativeCallback = { withdrawalState = false },
                isShown = withdrawalState
            ),
            hideDialog = { withdrawalState = false }
        )
        PasswordDialog(
            passwordValid = passwordValid,
            onNewValue = { password ->
                onEvent(ProfileEvent.Password(password))
            },
            isShown = passwordState,
            onPositiveCallback = {
                onEvent(ProfileEvent.Secession)
                passwordState = false
            },
            hideDialog = { passwordState = false }
        )
    }
}

@Preview
@Composable
private fun ProfilePreview(

) {
    ProfileScreen(
        user = MutableStateFlow(User.getInitValues()),
        passwordValid = MutableStateFlow(PasswordValid.Blank),
        anonymous = false,
        onEvent = {},
        navigateToEditUser = { _, _, _ -> },
        navigateToTerms = {}
    )
}