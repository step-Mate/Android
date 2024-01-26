package jinproject.stepwalk.login.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.screen.signup.EmailStatePreviewParameters
import jinproject.stepwalk.login.screen.signup.PasswordStatePreviewParameters
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.screen.state.SignValid
import jinproject.stepwalk.login.screen.state.isError
import jinproject.stepwalk.design.R.drawable as AppIcon

@Composable
internal fun InformationField(
    modifier: Modifier = Modifier,
    informationText : String,
    errorMessage : String = "",
    value: String,
    isError : Boolean = false,
    enabled : Boolean = true,
    errorColor : Color = MaterialTheme.colorScheme.error,
    keyboardType: KeyboardType = KeyboardType.Email,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    suffix : @Composable (() -> Unit)? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    onNewValue: (String) -> Unit
){
    OutlinedTextField(
        singleLine = true,
        modifier = modifier.fillMaxWidth(),
        value = value,
        isError = isError,
        enabled = enabled,
        textStyle = MaterialTheme.typography.bodyMedium,
        onValueChange = onNewValue,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        label = { Text(informationText, style = MaterialTheme.typography.bodyMedium) },
        supportingText = {
             if (isError){
                 DescriptionSmallText(text = errorMessage, color = errorColor)
             }
        },
        suffix = suffix,
        keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
        visualTransformation = visualTransformation,
        colors = TextFieldDefaults.colors(
            errorIndicatorColor = errorColor,
            errorLabelColor = errorColor,
            errorSupportingTextColor = errorColor,
            errorTextColor = errorColor
        )
    )
}

@Composable
internal fun IdField(
    value: String,
    onNewValue: (String) -> Unit,
    idValid: SignValid = SignValid.blank,
) {
    InformationField(
        informationText = "아이디",
        errorMessage = when (idValid){
            SignValid.notValid -> "잘못된 아이디 양식이에요. 영어,숫자,_만 입력가능하고 4~12글자까지 입력가능해요."
            SignValid.duplicationId -> "중복되는 아이디가 존재합니다."
            else -> "아이디를 입력해주세요."},
        value = value,
        isError = idValid.isError(),
        keyboardType = KeyboardType.Email,
        leadingIcon = { Icon(imageVector = Icons.Default.Person, contentDescription = "Email") },
        onNewValue = onNewValue
    )
}

@Composable
internal fun PasswordField(informationText: String, value: String, onNewValue: (String) -> Unit, passwordValid: SignValid = SignValid.blank) {
    val isVisible = remember { mutableStateOf(false) }

    InformationField(
        informationText = informationText,
        errorMessage = when (passwordValid){
            SignValid.notValid -> "잘못된 비밀번호 양식이에요. 8~16글자까지 입력가능하고 영어,숫자,!@#\$%^&amp;*특수문자만 사용가능해요."
            SignValid.notMatch -> "비밀번호가 일치하지 않습니다."
            else -> "비밀번호를 입력해주세요."},
        value = value,
        isError = passwordValid.isError(),
        keyboardType = KeyboardType.Password,
        leadingIcon = { Icon(imageVector = Icons.Default.Lock, contentDescription = "Lock") },
        trailingIcon = {
            IconButton(onClick = { isVisible.value = !isVisible.value }) {
                Icon(imageVector =  if (isVisible.value) ImageVector.vectorResource(AppIcon.ic_visibility_on)
                else ImageVector.vectorResource(AppIcon.ic_visibility_off), contentDescription = "Visibility")
            }
        },
        visualTransformation = if (isVisible.value) VisualTransformation.None else PasswordVisualTransformation(),
        onNewValue = onNewValue
    )
}


@Composable
internal fun ColumnScope.EmailVerificationField(
    email: String,
    emailCode : String,
    emailValid : SignValid,
    emailCodeValid : SignValid,
    requestEmailVerification : () -> Unit,
    onEmailValue: (String) -> Unit,
    onVerificationCodeValue : (String) -> Unit
){
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        InformationField(
            informationText = "이메일 입력",
            modifier = Modifier.weight(0.7f),
            value = email,
            onNewValue = onEmailValue,
            enabled = emailCodeValid != SignValid.success,
            isError = emailCodeValid == SignValid.success || emailValid == SignValid.notValid || emailValid == SignValid.verifying,
            errorMessage = when {
                emailCodeValid == SignValid.success -> "이메일이 인증되었습니다."
                emailValid == SignValid.notValid -> "잘못된 이메일 양식입니다."
                emailValid == SignValid.verifying -> "메일함을 확인후 코드를 입력해주세요."
                else -> ""
            },
            errorColor = when {
                emailCodeValid == SignValid.success -> StepWalkColor.success.color
                emailValid == SignValid.notValid -> MaterialTheme.colorScheme.error
                else -> StepWalkColor.blue_400.color
            }
        )
        EnableButton(
            text = "인증",
            modifier = Modifier
                .weight(0.3f)
                .fillMaxHeight(0.8f)
                .padding(start = 10.dp, bottom = 5.dp),
            enabled = emailValid == SignValid.success
        ) {
            requestEmailVerification()//서버에서 이메일 코드처리
        }
    }
    AnimatedVisibility(
        visible = if (emailCodeValid == SignValid.success) false else emailValid == SignValid.verifying || emailCodeValid == SignValid.notValid,
        enter = slideInVertically { -it },
        exit = slideOutVertically { -it }
    ) {
        InformationField(
            informationText = "인증코드 입력",
            errorMessage = "잘못된 인증코드 입니다.",
            value = emailCode,
            isError = emailCodeValid == SignValid.notValid,
            keyboardType = KeyboardType.NumberPassword,
            onNewValue = onVerificationCodeValue
        )
    }
}

@Composable
@Preview
private fun PreviewInformationField(

) = StepWalkTheme {
    Column {
        InformationField(
            informationText = "몸무게",
            errorMessage = "잘못된 양식입니다.",
            value = "",
            isError = true,
            suffix = { DescriptionSmallText(text = "kg")},
            onNewValue = {}
        )
    }
}

@Composable
@Preview
private fun PreviewIdField(

) = StepWalkTheme {
    Column {
        IdField(value = "", onNewValue = {})
    }
}

@Composable
@Preview
private fun PreviewPasswordField(
    @PreviewParameter(PasswordStatePreviewParameters::class)
    state : Account
) = StepWalkTheme {
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
    state : List<Account>
) = StepWalkTheme {
    Column {
        EmailVerificationField(
            email = state.first().now(),
            emailCode = state.last().now(),
            emailValid = state.first().nowValid(),
            emailCodeValid = state.last().nowValid(),
            requestEmailVerification = { },
            onEmailValue = {},
            onVerificationCodeValue = {}
        )
    }
}