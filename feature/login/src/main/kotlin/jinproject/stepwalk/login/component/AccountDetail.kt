package jinproject.stepwalk.login.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.login.screen.signup.SignValid
import jinproject.stepwalk.login.screen.signup.isError

@Composable
internal fun IdDetail(
    id : () -> String,
    idValid : () -> SignValid,
    onNewIdValue : (String) -> Unit,
){
    IdField(
        value = id(),
        onNewValue = onNewIdValue,
        isError = idValid().isError(),
    )
    ErrorMessage(
        message = when (idValid()){
            SignValid.notValid -> R.string.not_valid_id_content
            SignValid.duplicationId -> R.string.duplication_id
            SignValid.success -> R.string.not_duplication_id
            else -> R.string.blank_id_content },
        isError = idValid().isError()
    )
}

@Composable
internal fun PasswordDetail(
    password : () -> String,
    repeatPassword : () -> String,
    passwordValid : () -> SignValid,
    repeatPasswordValid: () -> SignValid,
    onNewPassword : (String) -> Unit,
    onNewRepeatPassword : (String) -> Unit
){
    PasswordField(
        value = password(),
        onNewValue = onNewPassword,
        isError = passwordValid().isError()
    )
    ErrorMessage(
        message = when (passwordValid()){
            SignValid.notValid -> R.string.not_valid_password_content
            else -> R.string.blank_id_content },
        isError = passwordValid().isError()
    )
    RepeatPasswordField(
        value = repeatPassword(),
        onNewValue = onNewRepeatPassword,
        isError = repeatPasswordValid().isError()
    )
    ErrorMessage(
        message = when (repeatPasswordValid()){
            SignValid.notValid -> R.string.not_valid_password_content
            SignValid.notMatch -> R.string.not_match_password
            else -> R.string.blank_id_content },
        isError = repeatPasswordValid().isError()
    )
}

@Composable
internal fun IdResultDetail(
    findAccountId : () -> String
){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = StepWalkColor.blue_200.color,
            contentColor = StepWalkColor.blue_400.color
        )
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "조회된 내역은 다음과 같습니다.", style = MaterialTheme.typography.bodyLarge)
            VerticalSpacer(height = 10.dp)
            Text(text = "아이디 : ${findAccountId()}", style = MaterialTheme.typography.bodyMedium)
        }
    }
}