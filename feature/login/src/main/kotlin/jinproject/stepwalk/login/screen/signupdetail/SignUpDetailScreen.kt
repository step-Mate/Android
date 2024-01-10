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
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
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
import jinproject.stepwalk.login.screen.state.UserDataValid
import jinproject.stepwalk.login.screen.state.isError

@Composable
internal fun SignUpDetailScreen(
    signUpDetailViewModel: SignUpDetailViewModel = hiltViewModel(),
    id : String,
    password : String,
    popBackStacks: (String,Boolean,Boolean) -> Unit
) {
    val nickname by signUpDetailViewModel.nickname.collectAsStateWithLifecycle()
    val age by signUpDetailViewModel.age.collectAsStateWithLifecycle()
    val height by signUpDetailViewModel.height.collectAsStateWithLifecycle()
    val weight by signUpDetailViewModel.weight.collectAsStateWithLifecycle()
    val email by signUpDetailViewModel.email.collectAsStateWithLifecycle()
    val emailCode by signUpDetailViewModel.emailCode.collectAsStateWithLifecycle()

    SignUpDetailScreen(
        signUpDetail = {SignUpDetail(id,password,nickname,age,height,weight,email,emailCode)},
        userValid = signUpDetailViewModel.valids,
        updateUserEvent = signUpDetailViewModel::updateUserEvent,
        requestEmailVerification = signUpDetailViewModel::requestEmailVerification,
        popBackStacks = popBackStacks
    )

}

@Composable
private fun SignUpDetailScreen(
    signUpDetail: () -> SignUpDetail,
    userValid: UserDataValid,
    updateUserEvent : (UserEvent,String) -> Unit,
    requestEmailVerification : () -> Unit,
    popBackStacks: (String,Boolean,Boolean) -> Unit
){
    val scrollState = rememberScrollState()

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
                modifier = Modifier.fillMaxWidth().padding(start = 12.dp)
            )
            VerticalSpacer(height = 30.dp)
            InformationField(
                informationText = "닉네임",
                errorMessage = "한글,영어,숫자가능,특수문자불가,2~10글자까지 입력가능",
                value = signUpDetail().nickname,
                isError = userValid.nicknameValid.value.isError()
            ){
                val text = it.trim()
                if (text.length <= MAX_NICKNAME_LENGTH)
                    updateUserEvent(UserEvent.nickname,text)
            }

            InformationField(
                informationText = "나이",
                errorMessage = "정확한 나이를 입력해주세요.",
                value = signUpDetail().age,
                isError = userValid.ageValid.value.isError(),
                keyboardType = KeyboardType.NumberPassword
            ){
                val text = it.trim()
                if (text.length <= MAX_AGE_LENGTH)
                    updateUserEvent(UserEvent.age,text)
            }

            InformationField(
                informationText = "키",
                errorMessage = "정확한 키를 입력해주세요.",
                value = signUpDetail().height,
                isError = userValid.heightValid.value.isError(),
                keyboardType = KeyboardType.Decimal
            ){
                val text = it.trim()
                if (text.length <= MAX_HEIGHT_LENGTH)
                    updateUserEvent(UserEvent.height,text)
            }

            InformationField(
                informationText = "몸무게",
                errorMessage = "정확한 몸무게를 입력해주세요.",
                value = signUpDetail().weight,
                isError = userValid.weightValid.value.isError(),
                keyboardType = KeyboardType.Decimal
            ){
                val text = it.trim()
                if (text.length <= MAX_WEIGHT_LENGTH)
                    updateUserEvent(UserEvent.weight,text)
            }
            
            EmailVerificationField(
                email = {signUpDetail().email},
                verificationCode = {signUpDetail().emailCode},
                isVerification = {userValid.emailValid.value},
                requestEmailVerification = requestEmailVerification,
                onEmailValue = {updateUserEvent(UserEvent.email,it)},
                onVerificationCodeValue = {updateUserEvent(UserEvent.emailCode,it)}
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
                enabled = userValid.isSuccessfulValid()
            ) {
                //서버로 데이터 전송 대기후 이동
                popBackStacks("home",false,false)//추후 수정??
            }
        }
    }
}

@Composable
@Preview
private fun PreviewSignUpDetailScreen(

) = StepWalkTheme {
    SignUpDetailScreen(
        signUpDetail = {SignUpDetail()},
        userValid = UserDataValid(),
        updateUserEvent = {_,_ ->},
        requestEmailVerification = {},
        popBackStacks = {_,_,_ ->}
    )
}