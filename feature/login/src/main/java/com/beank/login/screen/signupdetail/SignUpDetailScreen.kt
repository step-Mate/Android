package com.beank.login.screen.signupdetail

import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import jinproject.stepwalk.design.theme.StepWalkTheme

@Composable
internal fun SignUpDetailScreen(
    signUpDetailViewModel: SignUpDetailViewModel = hiltViewModel(),
    id : String,
    password : String
) {

    SignUpDetailScreen()

}

@Composable
private fun SignUpDetailScreen(

){

}

@Composable
@Preview
private fun PreviewSignUpScreen(

) = StepWalkTheme {
    SignUpDetailScreen()
}