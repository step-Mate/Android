package jinproject.stepwalk.login.screen.signupdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
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
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.component.EmailVerificationField
import jinproject.stepwalk.login.component.InformationField
import jinproject.stepwalk.login.screen.state.Account
import jinproject.stepwalk.login.screen.state.isError
import jinproject.stepwalk.login.utils.MAX_EMAIL_CODE_LENGTH
import jinproject.stepwalk.login.utils.MAX_EMAIL_LENGTH

@Composable
internal fun SignUpDetailScreen(
    signUpDetailViewModel: SignUpDetailViewModel = hiltViewModel(),
    popBackStacks: (String,Boolean,Boolean) -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit
) {
    val state by signUpDetailViewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = state){
        if (state.isSuccess){
            popBackStacks("home",false,false)
        }else{
            if (state.errorMessage.isNotEmpty()){
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
        onEvent = signUpDetailViewModel::onEvent
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
    onEvent : (SignUpDetailEvent) -> Unit
){
    val scrollState = rememberScrollState()
    val nicknameValue by nickname.value.collectAsStateWithLifecycle()
    val ageValue by age.value.collectAsStateWithLifecycle()
    val heightValue by height.value.collectAsStateWithLifecycle()
    val weightValue by weight.value.collectAsStateWithLifecycle()
    val emailValue by email.value.collectAsStateWithLifecycle()
    val emailCodeValue by emailCode.value.collectAsStateWithLifecycle()

    DefaultLayout(
        contentPaddingValues = PaddingValues(vertical = 60.dp)
    ) {
        Column(
            modifier = Modifier
                .imePadding()
                .verticalScroll(scrollState)
        ) {
            Text(
                text = stringResource(id = R.string.signup_title),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 12.dp)
            )
            VerticalSpacer(height = 30.dp)
            InformationField(
                informationText = "닉네임",
                errorMessage = "한글,영어,숫자가능,특수문자불가,2~10글자까지 입력가능",
                value = nicknameValue,
                isError = nickname.valid.isError()
            ){
                val text = it.trim()
                if (text.length <= MAX_NICKNAME_LENGTH)
                    onEvent(SignUpDetailEvent.nickname(text))
            }

            InformationField(
                informationText = "나이",
                errorMessage = "정확한 나이를 입력해주세요.",
                value = ageValue,
                isError = age.valid.isError(),
                keyboardType = KeyboardType.NumberPassword
            ){
                val text = it.trim()
                if (text.length <= MAX_AGE_LENGTH)
                    onEvent(SignUpDetailEvent.age(text))
            }

            InformationField(
                informationText = "키",
                errorMessage = "정확한 키를 입력해주세요.",
                value = heightValue,
                isError = height.valid.isError(),
                keyboardType = KeyboardType.Decimal
            ){
                val text = it.trim()
                if (text.length <= MAX_HEIGHT_LENGTH)
                    onEvent(SignUpDetailEvent.height(text))
            }

            InformationField(
                informationText = "몸무게",
                errorMessage = "정확한 몸무게를 입력해주세요.",
                value = weightValue,
                isError = weight.valid.isError(),
                keyboardType = KeyboardType.Decimal
            ){
                val text = it.trim()
                if (text.length <= MAX_WEIGHT_LENGTH)
                    onEvent(SignUpDetailEvent.weight(text))
            }
            
            EmailVerificationField(
                email = emailValue,
                emailCode = emailCodeValue,
                emailValid = email.valid,
                emailCodeValid = emailCode.valid,
                requestEmailVerification = {onEvent(SignUpDetailEvent.requestEmail)},
                onEmailValue = {
                    val text = it.trim()
                    if (text.length <= MAX_EMAIL_LENGTH)
                        onEvent(SignUpDetailEvent.email(text))
                },
                onVerificationCodeValue = {
                    val text = it.trim()
                    if (text.length <= MAX_EMAIL_CODE_LENGTH)
                        onEvent(SignUpDetailEvent.emailCode(text))
                }
            )
        }

        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxHeight()
        ) {
            EnableButton(
                text = "계정 생성",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(50.dp),
                enabled = nickname.isSuccessful() && age.isSuccessful() && height.isSuccessful() && weight.isSuccessful() &&
                email.isSuccessful() && emailCode.isSuccessful()
            ) {
                onEvent(SignUpDetailEvent.signUp)
            }
        }
    }
}

@Composable
@Preview
private fun PreviewSignUpDetailScreen(

) = StepWalkTheme {
    SignUpDetailScreen(
        nickname = Account(500),
        age = Account(500),
        height = Account(500),
        weight = Account(500),
        email = Account(500),
        emailCode = Account(500),
        onEvent = {}
    )
}