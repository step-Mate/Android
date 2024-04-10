package com.stepmate.profile.screen.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import com.stepmate.profile.screen.profile.ProfileViewModel.Companion.CANNOT_LOGIN_EXCEPTION
import com.stepmate.profile.screen.profile.component.PasswordDialog
import com.stepmate.profile.screen.profile.component.ProfileButton
import com.stepmate.profile.screen.profile.component.ProfileDetail
import com.stepmate.profile.screen.profile.component.ProfileEnterButton
import com.stepmate.core.SnackBarMessage
import com.stepmate.design.component.DialogState
import com.stepmate.design.component.HeadlineText
import com.stepmate.design.component.StepMateDialog
import com.stepmate.design.component.StepMateProgressIndicatorRotating
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.design.component.layout.DefaultLayout
import com.stepmate.domain.model.BodyData
import com.stepmate.domain.model.user.User
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
    val passwordValidState by passwordValid.collectAsStateWithLifecycle()
    var logoutState by remember { mutableStateOf(false) }
    var withdrawalState by remember { mutableStateOf(false) }
    var passwordState by remember { mutableStateOf(false) }


    LaunchedEffect(key1 = passwordValidState) {
        if (passwordValidState == PasswordValid.Success)
            passwordState = false
    }

    DefaultLayout(
        modifier = Modifier.statusBarsPadding(),
        contentPaddingValues = PaddingValues()
    ) {
        VerticalSpacer(height = 140.dp)
        ProfileDetail(
            user = user,
            bodyData = bodyData
        )
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
                    onEvent(ProfileEvent.MoveToEdit(navigateToEditUser, anonymous))
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
            content = "회원탈퇴를 할 경우 저장된 모든 개인정보와 데이터(걸음수, 레벨, 캐릭터 등)가 제거될수 있어요. 그래도 진행하시겠어요?",
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
private fun PreviewProfileLogin(
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

@Preview
@Composable
private fun PreviewProfileAnonymous(
) {
    ProfileScreen(
        user = MutableStateFlow(User.getInitValues()),
        passwordValid = MutableStateFlow(PasswordValid.Blank),
        bodyData = MutableStateFlow(BodyData()),
        anonymous = true,
        onEvent = {},
        navigateToEditUser = { _, _, _ -> },
        navigateToTerms = {},
        navigateToLogin = {}
    )
}