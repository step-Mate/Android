package jinproject.stepwalk.login.screen.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.login.screen.state.SignValid

@Composable
internal fun PasswordDetail(
    password : String,
    repeatPassword : String,
    passwordValid : SignValid,
    repeatPasswordValid: SignValid,
    onNewPassword : (String) -> Unit,
    onNewRepeatPassword : (String) -> Unit
){
    Column {
        PasswordField(
            informationText = "비밀번호",
            value = password,
            onNewValue = onNewPassword,
            passwordValid = passwordValid
        )
        PasswordField(
            informationText = "비밀번호 확인",
            value = repeatPassword,
            onNewValue = onNewRepeatPassword,
            passwordValid = repeatPasswordValid
        )
    }
}

@Composable
internal fun IdResultDetail(
    findAccountId : String
){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .clip(RoundedCornerShape(5.dp))
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 12.dp, vertical = 5.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "조회된 내역은 다음과 같습니다.", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.onSurface, modifier = Modifier.padding(bottom = 10.dp))
        Text(text = "아이디 : $findAccountId", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurface)
    }
}


@Composable
@Preview
private fun PreviewPasswordDetail(

) = StepWalkTheme {
    PasswordDetail(
        password = "",
        repeatPassword =  "",
        passwordValid =  SignValid.notValid ,
        repeatPasswordValid = SignValid.notMatch ,
        onNewPassword = {},
        onNewRepeatPassword = {}
    )
}

@Composable
@Preview
private fun PreviewIdResultDetail(

) = StepWalkTheme {
    IdResultDetail("testID")
}