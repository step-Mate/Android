package jinproject.stepwalk.home.screen.home.component.userinfo

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.FooterText
import jinproject.stepwalk.design.component.HorizontalWeightSpacer
import jinproject.stepwalk.design.component.VerticalWeightSpacer
import jinproject.stepwalk.design.component.layout.StepFooterLayout
import jinproject.stepwalk.design.component.layout.StepLayout
import jinproject.stepwalk.design.component.layout.StepProgress
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.home.HomeUiState
import jinproject.stepwalk.home.screen.home.HomeUiStatePreviewParameters
import jinproject.stepwalk.home.screen.home.state.HealthTab

@Composable
internal fun UserInfoLayout(
    modifier: Modifier = Modifier,
    step: HealthTab,
) {
    val progress = (step.header.total.toFloat() / step.header.goal.toFloat()).coerceIn(0f, 1f)

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("ic_anim_running_1.json"))
    val lottieProgress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = modifier
            .fillMaxWidth()
            .height(116.dp),
        verticalArrangement = Arrangement.Center
    ) {
        UserStatus(
            modifier = Modifier
                .fillMaxWidth()
                .height(28.dp),
            name = "홍길동",
        )
        VerticalWeightSpacer(float = 1f)
        StepLayout(
            modifier = Modifier.fillMaxWidth(),
            characterContent = {
                LottieAnimation(
                    composition = composition,
                    progress = { lottieProgress },
                    modifier = Modifier.size(48.dp)
                )
            },
            progressContent = { p ->
                StepProgress(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    progress = p
                )
            },
            progress = progress
        )
        StepFooterLayout(
            totalContent = {
                FooterText(
                    text = step.header.total.toString(),
                )
            },
            goalContent = {
                FooterText(text = step.header.goal.toString())
            },
            separatorContent = {
                FooterText(text = " / ")
            },
            progress = progress,
        )
    }
}

@Composable
internal fun UserStatus(
    modifier: Modifier = Modifier,
    name: String,
) {
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
        ) {
            Row(
                verticalAlignment = Alignment.Bottom,
            ) {
                DescriptionLargeText(text = name)
                FooterText(text = "님")
            }
            FooterText(text = "오늘도 즐거운 하루 되세요!", modifier = Modifier.padding(start = 2.dp))
        }
        HorizontalWeightSpacer(float = 1f)
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewUserInfo(
    @PreviewParameter(HomeUiStatePreviewParameters::class, limit = 1)
    uiState: HomeUiState,
) = StepWalkTheme {
    UserInfoLayout(
        step = uiState.step
    )
}