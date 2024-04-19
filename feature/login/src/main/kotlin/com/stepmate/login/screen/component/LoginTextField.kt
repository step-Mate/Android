package com.stepmate.login.screen.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stepmate.design.component.DefaultOutlinedTextField
import com.stepmate.design.theme.StepWalkColor
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.login.screen.signup.EmailStatePreviewParameters
import com.stepmate.login.screen.signup.PasswordStatePreviewParameters
import com.stepmate.login.screen.state.Account
import com.stepmate.login.screen.state.SignValid
import com.stepmate.login.screen.state.isError
import com.stepmate.design.R.drawable as AppIcon

@Composable
internal fun IdField(
    value: String,
    onNewValue: (String) -> Unit,
    idValid: SignValid = SignValid.blank,
) {
    DefaultOutlinedTextField(
        informationText = "아이디",
        errorMessage = when (idValid) {
            SignValid.notValid -> "잘못된 아이디 양식이에요. 영어,숫자,_만 입력가능하고 4~12글자까지 입력가능해요."
            SignValid.duplicationId -> "중복되는 아이디가 존재합니다."
            else -> "아이디를 입력해주세요."
        },
        value = value,
        isError = idValid.isError(),
        keyboardType = KeyboardType.Email,
        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Email") },
        onNewValue = onNewValue
    )
}

@Composable
internal fun PasswordField(
    informationText: String,
    value: String,
    onNewValue: (String) -> Unit,
    passwordValid: SignValid = SignValid.blank
) {
    val isVisible = remember { mutableStateOf(false) }

    DefaultOutlinedTextField(
        informationText = informationText,
        errorMessage = when (passwordValid) {
            SignValid.notValid -> "잘못된 비밀번호 양식이에요. 8~16글자까지 입력가능하고 영어,숫자,!@#\$%^&amp;*특수문자만 사용가능해요."
            SignValid.notMatch -> "비밀번호가 일치하지 않습니다."
            else -> "비밀번호를 입력해주세요."
        },
        value = value,
        isError = passwordValid.isError(),
        keyboardType = KeyboardType.Password,
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock") },
        trailingIcon = {
            IconButton(onClick = { isVisible.value = !isVisible.value }) {
                Icon(
                    imageVector = if (isVisible.value) ImageVector.vectorResource(AppIcon.ic_visibility_on)
                    else ImageVector.vectorResource(AppIcon.ic_visibility_off),
                    contentDescription = "Visibility"
                )
            }
        },
        visualTransformation = if (isVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        onNewValue = onNewValue
    )
}

@Composable
internal fun ColumnScope.EmailVerificationField(
    email: Account,
    emailCode: Account,
    codeVisibility: Boolean = false,
    requestEmailVerification: () -> Unit,
    onEmailValue: (String) -> Unit,
    onVerificationCodeValue: (String) -> Unit
) {
    val emailValue by email.value.collectAsStateWithLifecycle()
    val emailCodeValue by emailCode.value.collectAsStateWithLifecycle()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        DefaultOutlinedTextField(
            informationText = "이메일 입력",
            modifier = Modifier.weight(0.7f),
            value = emailValue.text,
            onNewValue = onEmailValue,
            enabled = !(emailCodeValue.valid == SignValid.success || emailValue.valid == SignValid.verifying),
            isError = emailCodeValue.valid == SignValid.success || emailValue.valid == SignValid.notValid || emailValue.valid == SignValid.verifying,
            errorMessage = when {
                emailCodeValue.valid == SignValid.success -> "이메일이 인증되었습니다."
                emailValue.valid == SignValid.notValid -> "잘못된 이메일 양식입니다."
                emailValue.valid == SignValid.verifying -> "메일함을 확인후 코드를 입력해주세요."
                else -> ""
            },
            errorColor = when {
                emailCodeValue.valid == SignValid.success -> StepWalkColor.green_600.color
                emailValue.valid == SignValid.notValid -> MaterialTheme.colorScheme.error
                else -> StepWalkColor.blue_400.color
            }
        )
        EnableButton(
            text = "인증",
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight(0.8f)
                .padding(start = 10.dp, bottom = 5.dp),
            enabled = emailValue.valid == SignValid.success
        ) {
            requestEmailVerification()
        }
    }
    AnimatedVisibility(
        visible = if (emailCodeValue.valid == SignValid.success && !codeVisibility) false else if (codeVisibility) true else emailValue.valid == SignValid.verifying || emailCodeValue.valid == SignValid.notValid,
    ) {
        DefaultOutlinedTextField(
            informationText = "인증코드 입력",
            errorMessage = "잘못된 인증코드 입니다.",
            value = emailCodeValue.text,
            isError = emailCodeValue.valid == SignValid.notValid,
            keyboardType = KeyboardType.NumberPassword,
            onNewValue = onVerificationCodeValue
        )
    }
}

@Composable
@Preview
private fun PreviewIdField(

) = StepMateTheme {
    Column {
        IdField(value = "", onNewValue = {})
    }
}

@Composable
@Preview
private fun PreviewPasswordField(
    @PreviewParameter(PasswordStatePreviewParameters::class)
    state: Account
) = StepMateTheme {
    Column {
        PasswordField(
            informationText = "비밀번호",
            value = state.now(),
            onNewValue = {},
            passwordValid = state.nowValid()
        )
    }
}

@Composable
@Preview
private fun PreviewEmailValidField(
    @PreviewParameter(EmailStatePreviewParameters::class)
    state: List<Account>
) = StepMateTheme {
    Column {
        EmailVerificationField(
            email = state.first(),
            emailCode = state.last(),
            requestEmailVerification = { },
            onEmailValue = {},
            onVerificationCodeValue = {}
        )
    }
}