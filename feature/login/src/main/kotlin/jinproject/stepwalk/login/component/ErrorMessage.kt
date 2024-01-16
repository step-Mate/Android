package jinproject.stepwalk.login.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.screen.state.SignValid


@Composable
internal fun ErrorMessage(
    message : String,
    isError : Boolean = false,
    color : Color = MaterialTheme.colorScheme.error
){
    AnimatedVisibility(
        visible = isError,
        enter = slideInVertically { -it },
        exit = slideOutVertically { -it }
    ) {
        Column {
            DescriptionSmallText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp, vertical = 5.dp),
                text = message,
                color = color
            )
        }
    }
}

@Composable
internal fun EmailErrorMessage(
    errorMessage : String,
    verifyingMessage : String,
    successMessage : String,
    emailValid: SignValid,
    emailCodeValid : SignValid
){
    ErrorMessage(
        message = when {
            emailCodeValid == SignValid.success -> successMessage
            emailValid == SignValid.notValid -> errorMessage
            emailValid == SignValid.verifying -> verifyingMessage
            else -> ""
        },
        isError = emailCodeValid == SignValid.success || emailValid == SignValid.notValid || emailValid == SignValid.verifying,
        color = when {
            emailCodeValid == SignValid.success -> StepWalkColor.success.color
            emailValid == SignValid.notValid -> MaterialTheme.colorScheme.error
            else -> StepWalkColor.blue_400.color
        }
    )
}

@Composable
@Preview
private fun PreviewErrorMessage(

) = StepWalkTheme {
    ErrorMessage(message = "error message preview", true)
}

@Composable
@Preview
private fun PreviewEmailErrorMessage(

) = StepWalkTheme {
    EmailErrorMessage(
        errorMessage = "error message",
        verifyingMessage = "verifying message",
        successMessage = "success message",
        emailValid = SignValid.success,
        emailCodeValid = SignValid.success
    )
}


