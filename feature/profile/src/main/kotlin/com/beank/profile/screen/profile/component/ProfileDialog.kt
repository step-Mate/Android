package com.beank.profile.screen.profile.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.beank.profile.screen.profile.PasswordValid
import jinproject.stepwalk.core.MAX_PASS_LENGTH
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.DefaultOutlinedTextField
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.HorizontalDivider
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.component.clickableAvoidingDuplication
import jinproject.stepwalk.design.theme.StepWalkColor
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

@Composable
internal fun PasswordDialog(
    passwordValid: StateFlow<PasswordValid>,
    onNewValue: (String) -> Unit,
    isShown: Boolean,
    onPositiveCallback: () -> Unit,
    properties: DialogProperties = DialogProperties(),
    hideDialog: () -> Unit,
) {
    val passwordValidState by passwordValid.collectAsStateWithLifecycle()
    var password by remember { mutableStateOf("") }
    if (isShown)
        Dialog(
            onDismissRequest = {
                hideDialog()
            },
            properties = properties
        ) {
            Surface(
                shape = RoundedCornerShape(20.dp),
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface
            ) {
                Column(
                    modifier = Modifier.padding(top = 8.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    DescriptionLargeText(text = "회원 탈퇴")
                    VerticalSpacer(height = 12.dp)
                    DescriptionSmallText(
                        text = "회원 탈퇴를 위한 비밀번호를 입력해주세요.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    VerticalSpacer(height = 5.dp)
                    PasswordField(
                        value = password,
                        onNewValue = {
                            val text = it.trim()
                            if (text.length <= MAX_PASS_LENGTH) {
                                password = text
                                onNewValue(text)
                            }
                        },
                        passwordValid = passwordValidState
                    )
                    HorizontalDivider()
                    DescriptionSmallText(
                        text = "회원탈퇴",
                        color = StepWalkColor.blue_400.color,
                        modifier = Modifier
                            .padding(vertical = 15.dp)
                            .clickableAvoidingDuplication {
                                if (passwordValidState == PasswordValid.Valid)
                                    onPositiveCallback()
                            },
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
}

@Composable
internal fun PasswordField(
    value: String,
    onNewValue: (String) -> Unit,
    passwordValid: PasswordValid = PasswordValid.Blank
) {
    val isVisible = remember { mutableStateOf(false) }

    DefaultOutlinedTextField(
        modifier = Modifier.padding(horizontal = 15.dp),
        informationText = "비밀번호",
        errorMessage = when (passwordValid) {
            PasswordValid.NotValid -> "잘못된 비밀번호 양식이에요. 8~16글자까지 입력가능하고 영어,숫자,!@#\$%^&amp;*특수문자만 사용가능해요."
            PasswordValid.NotMatch -> "비밀번호가 일치하지 않습니다."
            else -> "비밀번호를 입력해주세요."
        },
        value = value,
        isError = passwordValid != PasswordValid.Blank && passwordValid != PasswordValid.Valid,
        keyboardType = KeyboardType.Password,
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock") },
        trailingIcon = {
            IconButton(onClick = { isVisible.value = !isVisible.value }) {
                Icon(
                    imageVector = if (isVisible.value) ImageVector.vectorResource(R.drawable.ic_visibility_on)
                    else ImageVector.vectorResource(R.drawable.ic_visibility_off),
                    contentDescription = "Visibility"
                )
            }
        },
        visualTransformation = if (isVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        onNewValue = onNewValue
    )
}

@Preview
@Composable
private fun PasswordDialogPreview(

) {
    PasswordDialog(
        passwordValid = MutableStateFlow(PasswordValid.Blank),
        onNewValue = {},
        isShown = true,
        onPositiveCallback = {},
        hideDialog = {}
    )
}