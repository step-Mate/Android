package jinproject.stepwalk.login.component

import androidx.annotation.StringRes
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.screen.state.Verification

@Composable
internal fun ErrorMessage(
    @StringRes message : Int,
    isError : Boolean = false,
    color : Color = MaterialTheme.colorScheme.error
){
    AnimatedVisibility(
        visible = isError,
        enter = slideInVertically { -it },
        exit = slideOutVertically { -it }
    ) {
        Column {
            VerticalSpacer(height = 5.dp)
            DescriptionSmallText(
                text = stringResource(id = message),
                color = color,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            )
            VerticalSpacer(height = 5.dp)
        }
    }
}

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
            VerticalSpacer(height = 5.dp)
            DescriptionSmallText(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                text = message,
                color = color
            )
            VerticalSpacer(height = 5.dp)
        }
    }
}

@Composable
internal fun EmailErrorMessage(
    errorMessage : String,
    verifyingMessage : String,
    successMessage : String,
    isVerification: Verification
){
    AnimatedVisibility(
        visible = isVerification == Verification.emailError || isVerification ==Verification.verifying || isVerification == Verification.success,
        enter = slideInVertically { -it },
        exit = slideOutVertically { -it }
    ) {
        Column {
            VerticalSpacer(height = 5.dp)
            DescriptionSmallText(
                text = when (isVerification) {
                    Verification.emailError -> errorMessage
                    Verification.verifying -> verifyingMessage
                    Verification.success -> successMessage
                    else -> ""
                },
                color = when (isVerification) {
                    Verification.emailError -> MaterialTheme.colorScheme.error
                    Verification.success -> StepWalkColor.success.color
                    else -> StepWalkColor.blue_400.color
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            )
        }
    }
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
        isVerification = Verification.verifying
    )
}


