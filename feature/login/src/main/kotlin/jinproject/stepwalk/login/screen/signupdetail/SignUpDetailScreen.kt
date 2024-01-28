package jinproject.stepwalk.login.screen.signupdetail

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.login.component.EnableButton
import jinproject.stepwalk.login.utils.MAX_AGE_LENGTH
import jinproject.stepwalk.login.utils.MAX_HEIGHT_LENGTH
import jinproject.stepwalk.login.utils.MAX_NICKNAME_LENGTH
import jinproject.stepwalk.login.utils.MAX_WEIGHT_LENGTH
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.component.EmailVerificationField
import jinproject.stepwalk.login.component.InformationField
import jinproject.stepwalk.login.component.LoginLayout
import jinproject.stepwalk.login.screen.signup.SignUpStatePreviewParameters
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.screen.state.isError
import jinproject.stepwalk.login.utils.MAX_EMAIL_CODE_LENGTH
import jinproject.stepwalk.login.utils.MAX_EMAIL_LENGTH

@Composable
internal fun SignUpDetailScreen(
    signUpDetailViewModel: SignUpDetailViewModel = hiltViewModel(),
    popBackStack : () -> Unit,
    popBackStacks: (String,Boolean,Boolean) -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    val state by signUpDetailViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state){
        if (state.isSuccess){
            popBackStacks("home",false,false)
        }else{
            if (state.errorMessage.isNotEmpty() && !state.isLoading){
                showSnackBar(SnackBarMessage(state.errorMessage))
            }
        }
    }

    SignUpDetailScreen(
        nickname = signUpDetailViewModel.nickname,
        age = signUpDetailViewModel.age,
        height = signUpDetailViewModel.height,
        weight = signUpDetailViewModel.weight,
        email = signUpDetailViewModel.email,
        emailCode = signUpDetailViewModel.emailCode,
        isLoading = state.isLoading,
        onEvent = signUpDetailViewModel::onEvent,
        popBackStack = popBackStack
    )
}

@Composable
private fun SignUpDetailScreen(
    nickname : Account,
    age : Account,
    height : Account,
    weight : Account,
    email : Account,
    emailCode : Account,
    isLoading : Boolean,
    onEvent : (SignUpDetailEvent) -> Unit,
    popBackStack : () -> Unit,
){
    val nicknameValue by nickname.value.collectAsStateWithLifecycle()
    val ageValue by age.value.collectAsStateWithLifecycle()
    val heightValue by height.value.collectAsStateWithLifecycle()
    val weightValue by weight.value.collectAsStateWithLifecycle()
    val emailValue by email.value.collectAsStateWithLifecycle()
    val emailCodeValue by emailCode.value.collectAsStateWithLifecycle()

    LoginLayout(
        text = "회원가입",
        modifier = Modifier.fillMaxSize(),
        content = {
            Text(
                text = stringResource(id = R.string.signup_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
            )
            VerticalSpacer(height = 30.dp)
            InformationField(
                informationText = "닉네임",
                errorMessage = "한글,영어,숫자가능,특수문자불가,2~10글자까지 입력가능",
                value = nicknameValue.text,
                isError = nicknameValue.valid.isError()
            ){
                val text = it.trim()
                if (text.length <= MAX_NICKNAME_LENGTH)
                    onEvent(SignUpDetailEvent.Nickname(text))
            }

            InformationField(
                informationText = "나이",
                errorMessage = "정확한 나이를 입력해주세요.",
                value = ageValue.text,
                isError = ageValue.valid.isError(),
                keyboardType = KeyboardType.NumberPassword
            ){
                val text = it.trim()
                if (text.length <= MAX_AGE_LENGTH)
                    onEvent(SignUpDetailEvent.Age(text))
            }

            InformationField(
                informationText = "키",
                errorMessage = "정확한 키를 입력해주세요.",
                value = heightValue.text,
                isError = heightValue.valid.isError(),
                keyboardType = KeyboardType.Decimal,
                suffix = {
                    DescriptionSmallText(text = "cm")
                }
            ){
                val text = it.trim()
                if (text.length <= MAX_HEIGHT_LENGTH)
                    onEvent(SignUpDetailEvent.Height(text))
            }

            InformationField(
                informationText = "몸무게",
                errorMessage = "정확한 몸무게를 입력해주세요.",
                value = weightValue.text,
                isError = weightValue.valid.isError(),
                keyboardType = KeyboardType.Decimal,
                suffix = {
                    DescriptionSmallText(text = "kg")
                }
            ){
                val text = it.trim()
                if (text.length <= MAX_WEIGHT_LENGTH)
                    onEvent(SignUpDetailEvent.Weight(text))
            }

            EmailVerificationField(
                email = emailValue.text,
                emailCode = emailCodeValue.text,
                emailValid = emailValue.valid,
                emailCodeValid = emailCodeValue.valid,
                requestEmailVerification = {onEvent(SignUpDetailEvent.RequestEmail)},
                onEmailValue = {
                    val text = it.trim()
                    if (text.length <= MAX_EMAIL_LENGTH)
                        onEvent(SignUpDetailEvent.Email(text))
                },
                onVerificationCodeValue = {
                    val text = it.trim()
                    if (text.length <= MAX_EMAIL_CODE_LENGTH)
                        onEvent(SignUpDetailEvent.EmailCode(text))
                }
            )
        },
        bottomContent = {
            EnableButton(
                text = "계정 생성",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                enabled = nickname.isSuccessful() && age.isSuccessful() && height.isSuccessful() && weight.isSuccessful() &&
                        email.isSuccessful() && emailCode.isSuccessful() && !isLoading,
                loading = isLoading
            ) {
                onEvent(SignUpDetailEvent.SignUp)
            }
        },
        popBackStack = popBackStack
    )
}

@Composable
@Preview
private fun PreviewSignUpDetailScreen(
    @PreviewParameter(SignUpStatePreviewParameters::class)
    state : Account
) = StepWalkTheme {
    SignUpDetailScreen(
        nickname = state,
        age = state,
        height = state,
        weight = state,
        email = state,
        emailCode = state,
        isLoading = false,
        onEvent = {},
        popBackStack = {}
    )
}