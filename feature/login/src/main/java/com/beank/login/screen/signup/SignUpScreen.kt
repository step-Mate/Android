package com.beank.login.screen.signup

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
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
    var kakaoSignUp by rememberSaveable {
        mutableStateOf(true)
    }
    val id by signUpViewModel.id.collectAsStateWithLifecycle()
    val password by signUpViewModel.password.collectAsStateWithLifecycle()
    val repeatPassword by signUpViewModel.repeatPassword.collectAsStateWithLifecycle()


    LaunchedEffect(key1 = Unit){
        if (email.isNotBlank()){
            signUpViewModel.updateIdValue(email)
            kakaoSignUp = false
        }
    }

    SignUpScreen(
        kakaoSignUp = kakaoSignUp,
        signUp = SignUp(id, password, repeatPassword),
        valids = signUpViewModel.valids,
        updateIdValue = signUpViewModel::updateIdValue,
        updatePasswordValue = signUpViewModel::updatePasswordValue,
        updateRepeatPasswordValue = signUpViewModel::updateRepeatPasswordValue,
        navigateToSignUpDetail = navigateToSignUpDetail
    )
}

@Composable
private fun SignUpScreen(
    kakaoSignUp : Boolean,
    signUp: SignUp,
    valids : ValidValue,
    updateIdValue : (String) -> Unit,
    updatePasswordValue : (String) -> Unit,
    updateRepeatPasswordValue : (String) -> Unit,
    navigateToSignUpDetail : (String,String) -> Unit
){

    DefaultLayout(
        contentPaddingValues = PaddingValues(vertical = 16.dp)
    ) {
        Text(text = stringResource(id = AppText.signup_title), style = MaterialTheme.typography.titleMedium)

        IdField(
            value = signUp.id,
            onNewValue = {
                val text = it.trim()
                if (text.length <= MAX_ID_LENGTH)
                    updateIdValue(text)
            },
            isError = valids.idValid.isError(),
            enable = kakaoSignUp
        )
        ErrorMessage(
            message = AppText.not_valid_id_content,
            isError = valids.idValid.isError()
        )
        PasswordField(
            value = signUp.password,
            onNewValue = {
                val text = it.trim()
                if (text.length <= MAX_PASS_LENGTH)
                    updatePasswordValue(text)
            },
            isError = valids.passwordValid.isError()
        )
        ErrorMessage(
            message = AppText.not_valid_password_content,
            isError = valids.passwordValid.isError()
        )
        RepeatPasswordField(
            value = signUp.repeatPassword,
            onNewValue = {
                val text = it.trim()
                if (text.length <= MAX_PASS_LENGTH)
                    updateRepeatPasswordValue(text)
            },
            isError = valids.repeatPasswordValid.isError()
        )
        ErrorMessage(
            message = AppText.not_match_password,
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
                    .padding(horizontal = 12.dp).padding(bottom = 16.dp),
                isEnable = valids.isSuccessfulValid()
            ) {
                navigateToSignUpDetail(signUp.id,signUp.password)
            }
        }
    }
}

data class SignUp(
    var id : String = "",
    var password : String = "",
    var repeatPassword : String = ""
)

@Composable
@Preview
private fun PreviewSignUpScreen(

) = StepWalkTheme {
    SignUpScreen(
        kakaoSignUp = true,
        signUp = SignUp(),
        valids = ValidValue(SignValid.success,SignValid.notValid,SignValid.success),
        updateIdValue = {},
        updatePasswordValue = {},
        updateRepeatPasswordValue = {},
        navigateToSignUpDetail = {_,_ ->}
    )
}