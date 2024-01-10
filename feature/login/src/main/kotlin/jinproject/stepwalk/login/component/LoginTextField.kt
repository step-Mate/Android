package jinproject.stepwalk.login.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.screen.state.Verification
import jinproject.stepwalk.design.R.drawable as AppIcon

@Composable
internal fun InformationField(
    informationText : String,
    errorMessage : String,
    value: String,
    isError : Boolean = false,
    keyboardType: KeyboardType = KeyboardType.Email,
    onNewValue: (String) -> Unit
){
    Column {
        OutlinedTextField(
            singleLine = true,
            modifier = Modifier.fieldModifier(),
            value = value,
            isError = isError,
            textStyle = MaterialTheme.typography.bodyMedium,
            onValueChange = onNewValue,
            label = { Text(informationText, style = MaterialTheme.typography.bodyMedium) },
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        )
        ErrorMessage(message = errorMessage, isError = isError)
    }
}

@Composable
internal fun IdField(value: String, onNewValue: (String) -> Unit, isError : Boolean = false) {
    OutlinedTextField(
        singleLine = true,
        modifier = Modifier.fieldModifier(),
        value = value,
        isError = isError,
        textStyle = MaterialTheme.typography.bodyMedium,
        onValueChange = onNewValue,
        placeholder = { Text("아이디", style = MaterialTheme.typography.bodyMedium) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Email") },
    )
}

@Composable
internal fun PasswordField(value: String, onNewValue: (String) -> Unit, isError : Boolean = false) {
    PasswordField(value, "비밀번호", onNewValue, isError = isError)
}

@Composable
internal fun RepeatPasswordField(value: String, onNewValue: (String) -> Unit, isError : Boolean = false) {
    PasswordField(value, "비밀번호 확인", onNewValue, isError = isError)
}

@Composable
private fun PasswordField(
    value: String,
    placeholder: String,
    onNewValue: (String) -> Unit,
    isError : Boolean = false
) {
    var isVisible by remember { mutableStateOf(false) }

    OutlinedTextField(
        modifier = Modifier.fieldModifier(),
        value = value,
        onValueChange = onNewValue,
        textStyle = MaterialTheme.typography.bodyMedium,
        placeholder = { Text(text = placeholder, style = MaterialTheme.typography.bodyMedium) },
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock") },
        trailingIcon = {
            IconButton(onClick = { isVisible = !isVisible }) {
                Icon(imageVector =  if (isVisible) ImageVector.vectorResource(AppIcon.ic_visibility_on)
                else ImageVector.vectorResource(AppIcon.ic_visibility_off), contentDescription = "Visibility")
            }
        },
        isError = isError,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
        visualTransformation = if (isVisible) VisualTransformation.None else PasswordVisualTransformation()
    )
}

@Composable
internal fun EmailVerificationField(
    email: () -> String,
    verificationCode : () -> String,
    isVerification: () -> Verification,
    requestEmailVerification : () -> Unit,
    keyboardType: KeyboardType = KeyboardType.Email,
    onEmailValue: (String) -> Unit,
    onVerificationCodeValue : (String) -> Unit
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Max),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                singleLine = true,
                modifier = Modifier.weight(0.7f),
                value = email(),
                enabled = isVerification() != Verification.success,
                isError = isVerification() == Verification.emailError,
                textStyle = MaterialTheme.typography.bodyMedium,
                onValueChange = onEmailValue,
                label = {Text(text = "이메일 입력", style = MaterialTheme.typography.bodyMedium)},
                keyboardOptions = KeyboardOptions(keyboardType = keyboardType)
            )
            EnableButton(
                text = "인증",
                modifier = Modifier
                    .fillMaxHeight()
                    .weight(0.3f)
                    .padding(start = 10.dp, top = 10.dp),
                enabled = isVerification() == Verification.emailValid
            ) {
                requestEmailVerification()//서버에서 이메일 코드처리
            }
        }
        EmailErrorMessage(
            errorMessage = "잘못된 이메일 양식입니다.",
            verifyingMessage = "메일함을 확인후 코드를 입력해주세요.",
            successMessage = "이메일이 인증되었습니다.",
            isVerification = isVerification()
        )
        AnimatedVisibility(
            visible = isVerification() == Verification.verifying || isVerification() == Verification.codeError,
            enter = slideInVertically { -it },
            exit = slideOutVertically { -it }
        ) {
            Column {
                OutlinedTextField(
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    value = verificationCode(),
                    isError = isVerification() == Verification.codeError,//수정
                    textStyle = MaterialTheme.typography.bodyMedium,
                    onValueChange = onVerificationCodeValue,
                    label = {Text(text = "인증코드 입력", style = MaterialTheme.typography.bodyMedium)},
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                )
                ErrorMessage(
                    message = "잘못된 인증코드입니다.",
                    isError = isVerification() == Verification.codeError
                )
            }
        }
    }
}

internal fun Modifier.fieldModifier(): Modifier {
    return this
        .fillMaxWidth()
        .padding(12.dp, 4.dp)
}

@Composable
@Preview
private fun PreviewInformationField(

) = StepWalkTheme {
    InformationField(
        informationText = "닉네임",
        errorMessage = "잘못된 양식입니다.",
        value = "",
        isError = true,
        onNewValue = {}
    )
}

@Composable
@Preview
private fun PreviewIdField(

) = StepWalkTheme {
    IdField(value = "", onNewValue = {},isError = true)
}

@Composable
@Preview
private fun PreviewPasswordField(

) = StepWalkTheme {
    PasswordField(value = "", onNewValue = {},isError = true)
}

@Composable
@Preview
private fun PreviewRepeatPasswordField(

) = StepWalkTheme {
    RepeatPasswordField(value = "", onNewValue = {},isError = true)
}

@Composable
@Preview
private fun PreviewEmailValidField(

) = StepWalkTheme {
    EmailVerificationField(
        email = { ""},
        verificationCode = { "" },
        isVerification = { Verification.verifying },
        requestEmailVerification = {  },
        onEmailValue = {},
        onVerificationCodeValue = {}
    )
}