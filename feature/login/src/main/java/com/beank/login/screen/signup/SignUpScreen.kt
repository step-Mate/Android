package com.beank.login.screen.signup

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.beank.login.component.EnableButton
import com.beank.login.component.ErrorMessage
import com.beank.login.utils.MAX_ID_LENGTH
import com.beank.login.utils.MAX_PASS_LENGTH
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.component.IdField
import jinproject.stepwalk.home.component.PasswordField
import jinproject.stepwalk.home.component.RepeatPasswordField
import jinproject.stepwalk.design.R.string as AppText

@Composable
internal fun SignUpScreen(
    signUpViewModel: SignUpViewModel = hiltViewModel(),
    email : String,
    navigateToSignUpDetail : (String,String) -> Unit
) {
    val kakaoSignUp = rememberSaveable {
        mutableStateOf(true)
    }
    val id by signUpViewModel.id.collectAsStateWithLifecycle()
    val password by signUpViewModel.password.collectAsStateWithLifecycle()
    val repeatPassword by signUpViewModel.repeatPassword.collectAsStateWithLifecycle()


    LaunchedEffect(key1 = Unit){
        if (email.isNotBlank()){
            signUpViewModel.updateAccountEvent(AccountEvent.id,email)
            kakaoSignUp.value = false
        }
    }

    SignUpScreen(
        kakaoSignUp = kakaoSignUp.value,
        signUp = SignUp(id, password, repeatPassword),
        valids = signUpViewModel.valids,
        updateAccountEvent = signUpViewModel::updateAccountEvent,
        navigateToSignUpDetail = navigateToSignUpDetail
    )
}

@Composable
private fun SignUpScreen(
    kakaoSignUp : Boolean,
    signUp: SignUp,
    valids : ValidValue,
    updateAccountEvent : (AccountEvent,String) -> Unit,
    navigateToSignUpDetail : (String,String) -> Unit
){

    DefaultLayout(
        contentPaddingValues = PaddingValues(vertical = 60.dp)
    ) {
        Text(
            text = stringResource(id = AppText.signup_title),
            style = MaterialTheme.typography.titleMedium
        )
        VerticalSpacer(height = 30.dp)

        IdField(
            value = signUp.id,
            onNewValue = {
                val text = it.trim()
                if (text.length <= MAX_ID_LENGTH)
                    updateAccountEvent(AccountEvent.id,text)
            },
            isError = valids.idValid.isError(),
            enable = kakaoSignUp
        )
        ErrorMessage(
            message = when (valids.idValid){
                SignValid.notValid -> AppText.not_valid_id_content
                SignValid.duplicationId -> AppText.duplication_id
                SignValid.success -> AppText.not_duplication_id
                else -> AppText.blank_id_content },
            isError = valids.idValid.isError()
        )
        Log.d("idvalue",valids.idValid.toString())
        PasswordField(
            value = signUp.password,
            onNewValue = {
                val text = it.trim()
                if (text.length <= MAX_PASS_LENGTH)
                    updateAccountEvent(AccountEvent.password,text)
            },
            isError = valids.passwordValid.isError()
        )
        ErrorMessage(
            message = when (valids.passwordValid){
                SignValid.notValid -> AppText.not_valid_password_content
                else -> AppText.blank_id_content },
            isError = valids.passwordValid.isError()
        )
        RepeatPasswordField(
            value = signUp.repeatPassword,
            onNewValue = {
                val text = it.trim()
                if (text.length <= MAX_PASS_LENGTH)
                    updateAccountEvent(AccountEvent.repeatPassword,text)
            },
            isError = valids.repeatPasswordValid.isError()
        )
        ErrorMessage(
            message = when (valids.repeatPasswordValid){
                SignValid.notValid -> AppText.not_valid_password_content
                SignValid.notMatch -> AppText.not_match_password
                else -> AppText.blank_id_content },
            isError = valids.repeatPasswordValid.isError()
        )

        Column(
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.fillMaxHeight()
        ) {
            EnableButton(
                text = AppText.signUp_next_button,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp).height(50.dp),
                isEnable = valids.isSuccessfulValid()
            ) {
                navigateToSignUpDetail(signUp.id,signUp.password)
            }
        }
    }
}

@Composable
@Preview
private fun PreviewSignUpScreen(

) = StepWalkTheme {
    SignUpScreen(
        kakaoSignUp = true,
        signUp = SignUp(),
        valids = ValidValue(SignValid.success,SignValid.notValid,SignValid.success),
        updateAccountEvent = {_,_ ->},
        navigateToSignUpDetail = {_,_ ->}
    )
}