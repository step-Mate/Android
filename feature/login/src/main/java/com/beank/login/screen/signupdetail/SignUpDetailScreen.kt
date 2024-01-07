package com.beank.login.screen.signupdetail

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
import com.beank.login.component.EnableButton
import com.beank.login.utils.MAX_AGE_LENGTH
import com.beank.login.utils.MAX_HEIGHT_LENGTH
import com.beank.login.utils.MAX_NICKNAME_LENGTH
import com.beank.login.utils.MAX_WEIGHT_LENGTH
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.component.InformationField
import jinproject.stepwalk.design.R.string as AppText


@Composable
internal fun SignUpDetailScreen(
    signUpDetailViewModel: SignUpDetailViewModel = hiltViewModel(),
    id : String,
    password : String
) {
    val nickname by signUpDetailViewModel.nickname.collectAsStateWithLifecycle()
    val age by signUpDetailViewModel.age.collectAsStateWithLifecycle()
    val height by signUpDetailViewModel.height.collectAsStateWithLifecycle()
    val weight by signUpDetailViewModel.weight.collectAsStateWithLifecycle()

    SignUpDetailScreen(
        signUpDetail = {SignUpDetail(id,password,nickname,age,height,weight)},
        userValid = signUpDetailViewModel.valids,
        updateUserEvent = signUpDetailViewModel::updateUserEvent
    )

}

@Composable
private fun SignUpDetailScreen(
    signUpDetail: () -> SignUpDetail,
    userValid: UserDataValid,
    updateUserEvent : (UserEvent,String) -> Unit
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
                style = MaterialTheme.typography.titleMedium
            )
            VerticalSpacer(height = 30.dp)
            InformationField(
                informationText = AppText.username,
                errorMessage = AppText.username_error,
                value = signUpDetail().nickname,
                isError = userValid.nicknameValid.value.isError()
            ){
                val text = it.trim()
                if (text.length <= MAX_NICKNAME_LENGTH)
                    updateUserEvent(UserEvent.nickname,text)
            }

            InformationField(
                informationText = AppText.age,
                errorMessage = AppText.age_error,
                value = signUpDetail().age,
                isError = userValid.ageValid.value.isError(),
                keyboardType = KeyboardType.NumberPassword
            ){
                val text = it.trim()
                if (text.length <= MAX_AGE_LENGTH)
                    updateUserEvent(UserEvent.age,text)
            }

            InformationField(
                informationText = AppText.height,
                errorMessage = AppText.height_error,
                value = signUpDetail().height,
                isError = userValid.heightValid.value.isError(),
                keyboardType = KeyboardType.Decimal
            ){
                val text = it.trim()
                if (text.length <= MAX_HEIGHT_LENGTH)
                    updateUserEvent(UserEvent.height,text)
            }

            InformationField(
                informationText = AppText.weight,
                errorMessage = AppText.weight_error,
                value = signUpDetail().weight,
                isError = userValid.weightValid.value.isError(),
                keyboardType = KeyboardType.Decimal
            ){
                val text = it.trim()
                if (text.length <= MAX_WEIGHT_LENGTH)
                    updateUserEvent(UserEvent.weight,text)
            }
        }


        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxHeight()
        ) {
            EnableButton(
                text = R.string.signUp_next_button,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
                    .height(50.dp),
                isEnable = userValid.isSuccessfulValid()
            ) {
                //navigateToSignUpDetail(signUp.id,signUp.password)
                //서버로 데이터 전송 대기후 이동
            }
        }
    }
}

@Composable
@Preview
private fun PreviewSignUpScreen(

) = StepWalkTheme {
    SignUpDetailScreen(
        signUpDetail = {SignUpDetail()},
        userValid = UserDataValid(),
        updateUserEvent = {_,_ ->}
    )
}