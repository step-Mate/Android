package jinproject.stepwalk.design.component.layout

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.HeadlineText
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme

@Composable
fun ExceptionScreen(
    assetFilePath: String = "error_black_cat.json",
    headlineMessage: String,
    causeMessage: String,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(assetFilePath))
    val lottieProgress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VerticalSpacer(height = 80.dp)
        LottieAnimation(
            composition = composition,
            progress = { lottieProgress },
            modifier = Modifier.height(300.dp)
        )
        VerticalSpacer(height = 30.dp)
        HeadlineText(
            text = headlineMessage,
        )
        VerticalSpacer(height = 20.dp)
        DescriptionSmallText(text = causeMessage)
        content()
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewExceptionScreen() = StepWalkTheme {
    ExceptionScreen(
        headlineMessage = "헤드라인 메세지",
        causeMessage = "컨텐트 메세지"
    ) {

    }
}