package com.beank.profile.screen.profile

import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.beank.profile.screen.profile.ProfileViewModel.Companion.CANNOT_LOGIN_EXCEPTION
import com.beank.profile.screen.profile.component.PasswordDialog
import com.beank.profile.screen.profile.component.ProfileButton
import com.beank.profile.screen.profile.component.ProfileEnterButton
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.DialogState
import jinproject.stepwalk.design.component.HeadlineText
import jinproject.stepwalk.design.component.StepMateDialog
import jinproject.stepwalk.design.component.StepMateProgressIndicatorRotating
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.domain.model.BodyData
import jinproject.stepwalk.domain.model.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun ProfileScreen(
    profileViewModel: ProfileViewModel = hiltViewModel(),
    navigateToEditUser: (String, String, Boolean) -> Unit,
    navigateToTerms: () -> Unit,
    navigateToLogin: (NavOptions?) -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    val uiState by profileViewModel.uiState.collectAsStateWithLifecycle()

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
                bodyData = profileViewModel.bodyData,
                anonymous = false,
                onEvent = profileViewModel::onEvent,
                navigateToEditUser = navigateToEditUser,
                navigateToTerms = navigateToTerms,
                navigateToLogin = navigateToLogin
            )
        }

        ProfileViewModel.UiState.Anonymous, is ProfileViewModel.UiState.Error -> {
            ProfileScreen(
                user = profileViewModel.user,
                passwordValid = profileViewModel.passwordValid,
                bodyData = profileViewModel.bodyData,
                anonymous = true,
                onEvent = profileViewModel::onEvent,
                navigateToEditUser = navigateToEditUser,
                navigateToTerms = navigateToTerms,
                navigateToLogin = navigateToLogin
            )
        }
    }
}

@Composable
private fun ProfileScreen(
    user: StateFlow<User>,
    passwordValid: StateFlow<PasswordValid>,
    bodyData: StateFlow<BodyData>,
    anonymous: Boolean,
    onEvent: (ProfileEvent) -> Unit,
    navigateToEditUser: (String, String, Boolean) -> Unit,
    navigateToTerms: () -> Unit,
    navigateToLogin: (NavOptions?) -> Unit,
) {
    val userState by user.collectAsStateWithLifecycle()
    val passwordValidState by passwordValid.collectAsStateWithLifecycle()
    val bodyDataState by bodyData.collectAsStateWithLifecycle()
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(userState.character))
    val lottieProgress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    val backgroundCircle = MaterialTheme.colorScheme.primaryContainer
    val background = MaterialTheme.colorScheme.surface
    var logoutState by remember { mutableStateOf(false) }
    var withdrawalState by remember { mutableStateOf(false) }
    var passwordState by remember { mutableStateOf(false) }

    val textStyle = MaterialTheme.typography.bodyMedium
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(userState.level.toString(), textStyle) {
        textMeasurer.measure("LV.${userState.level}", textStyle)
    }

    LaunchedEffect(key1 = passwordValidState) {
        if (passwordValidState == PasswordValid.Success)
            passwordState = false
    }

    DefaultLayout(
        contentPaddingValues = PaddingValues()
    ) {
        VerticalSpacer(height = 140.dp)
        Box {
            Column(
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp))
                    .background(color = MaterialTheme.colorScheme.surface)
                    .align(Alignment.CenterStart)
                    .fillMaxWidth()
            ) {
                DescriptionLargeText(
                    modifier = Modifier.padding(start = 30.dp, top = 50.dp),
                    text = userState.designation,
                    color = MaterialTheme.colorScheme.onSurface
                )
                DescriptionLargeText(
                    modifier = Modifier.padding(start = 30.dp, top = 5.dp),
                    text = userState.name,
                    color = MaterialTheme.colorScheme.onSurface
                )
                DescriptionLargeText(
                    modifier = Modifier.padding(start = 30.dp, top = 5.dp, bottom = 20.dp),
                    text = "${bodyDataState.age}/${bodyDataState.height}/${bodyDataState.weight}",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
            LottieAnimation(
                composition = composition,
                progress = { lottieProgress },
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .offset(y = (-90).dp)
                    .size(110.dp)
                    .drawBehind {
                        drawCircle(
                            color = background,
                            radius = 190.dp.value
                        )
                        drawCircle(
                            color = backgroundCircle,
                            radius = 190.dp.value,
                            style = Stroke(width = 5f)
                        )
                        drawText(
                            textLayoutResult = textLayoutResult,
                            topLeft = Offset(
                                x = center.x - textLayoutResult.size.width / 2,
                                y = this.size.height * 0.85f
                            )
                        )
                    }
            )
        }

        Column(
            modifier = Modifier
                .padding(top = 50.dp, start = 12.dp, end = 12.dp)
                .shadow(elevation = 4.dp, shape = RoundedCornerShape(12.dp))
                .background(color = MaterialTheme.colorScheme.surface)

        ) {
            HeadlineText(
                text = "내 정보",
                modifier = Modifier.padding(top = 20.dp, start = 20.dp)
            )
            VerticalSpacer(height = 10.dp)
            ProfileEnterButton(
                text = "내 정보 수정",
                onClick = {
                    navigateToEditUser(userState.name, userState.designation, anonymous)
                }
            )
            ProfileEnterButton(
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
            } else {
                ProfileEnterButton(
                    text = "로그인",
                    onClick = { navigateToLogin(null) }
                )
            }
            VerticalSpacer(height = 10.dp)
        }

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
        },
        hideDialog = { passwordState = false }
    )
}

@Preview
@Composable
private fun ProfilePreview(

) {
    ProfileScreen(
        user = MutableStateFlow(User.getInitValues()),
        passwordValid = MutableStateFlow(PasswordValid.Blank),
        bodyData = MutableStateFlow(BodyData()),
        anonymous = false,
        onEvent = {},
        navigateToEditUser = { _, _, _ -> },
        navigateToTerms = {},
        navigateToLogin = {}
    )
}