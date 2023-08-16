package jinproject.stepwalk.app.ui.home.component

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.design.theme.Typography

@Composable
fun UserSteps(
    step: Long
) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Text(
            text = "현재 걸음 수 : $step",
            style = Typography.bodySmall
        )
    }
}

@Composable
@Preview
fun PreviewUserSteps() = StepWalkTheme {
    UserSteps(step = 100L)
}