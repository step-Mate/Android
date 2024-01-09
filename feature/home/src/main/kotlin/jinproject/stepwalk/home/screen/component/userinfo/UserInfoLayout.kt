package jinproject.stepwalk.home.screen.component.userinfo

import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieClipSpec
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieAnimatable
import com.airbnb.lottie.compose.rememberLottieComposition
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.DefaultIconButton
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.FooterText
import jinproject.stepwalk.design.component.HorizontalWeightSpacer
import jinproject.stepwalk.design.component.VerticalWeightSpacer
import jinproject.stepwalk.design.component.asLoose
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.HomeUiState
import jinproject.stepwalk.home.screen.HomeUiStatePreviewParameters
import jinproject.stepwalk.home.screen.state.HealthTab
import kotlin.math.roundToInt

@Composable
internal fun UserInfoLayout(
    modifier: Modifier = Modifier,
    step: HealthTab,
) {
    val progress = (step.header.total.toFloat() / step.header.goal.toFloat()).coerceIn(0f, 1f)

    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("ic_anim_running_1.json"))
    val lottieProgress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)

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
            badge = R.drawable.ic_heart_solid,
            achieveDegree = 30,
            achieveMax = 200
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
            progressContent = {
                StepProgress(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(20.dp),
                    progress = progress
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
    badge: Int,
    achieveDegree: Int,
    achieveMax: Int,
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
        Icon(
            painter = painterResource(id = badge),
            contentDescription = "UserBadgeIcon",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(24.dp)
        )

        DescriptionSmallText(text = "달성도 $achieveDegree / $achieveMax")
    }
}

@Composable
internal fun StepFooterLayout(
    totalContent: @Composable () -> Unit,
    goalContent: @Composable () -> Unit,
    separatorContent: @Composable () -> Unit,
    progress: Float,
) {
    Layout(
        contents = listOf(
            totalContent,
            goalContent,
            separatorContent,
        ),
    ) { (totalMeasurables, goalMeasurables, separatorMeasurables), constraints ->
        val loosedConstraints = constraints.asLoose()
        val totalPlaceable = totalMeasurables.first().measure(loosedConstraints)
        val goalPlaceable = goalMeasurables.first().measure(loosedConstraints)
        val separatorPlaceable = separatorMeasurables.first().measure(loosedConstraints)
        val maxWidth = constraints.maxWidth

        layout(maxWidth, totalPlaceable.height) {
            val totalXPos = if (progress == 1f)
                (maxWidth * progress).roundToInt() - totalPlaceable.width
            else
                (maxWidth * progress).roundToInt() - totalPlaceable.width / 2

            val goalPlaceableXPos = maxWidth - goalPlaceable.width

            when {
                maxWidth - totalXPos < goalPlaceable.width -> {
                    val separatorXPos = goalPlaceableXPos - separatorPlaceable.width
                    totalPlaceable.place(
                        x = separatorXPos - totalPlaceable.width,
                        y = 0
                    )
                    separatorPlaceable.place(
                        x = separatorXPos,
                        y = 0
                    )
                }

                else -> {
                    totalPlaceable.place(
                        x = totalXPos,
                        y = 0
                    )
                }
            }

            if (progress != 1f)
                goalPlaceable.place(x = goalPlaceableXPos, y = 0)
        }
    }
}

@Composable
internal fun StepLayout(
    modifier: Modifier = Modifier,
    characterContent: @Composable () -> Unit,
    progressContent: @Composable () -> Unit,
    progress: Float,
) {
    Layout(
        contents = listOf(
            characterContent,
            progressContent,
        ),
        modifier = modifier
    ) { (characterMeasurables, progressMeasurables), constraints ->
        val loosedConstraints = constraints.asLoose()
        val characterPlaceable = characterMeasurables.first().measure(loosedConstraints)
        val progressPlaceable = progressMeasurables.first().measure(loosedConstraints)

        val maxWidth = progressPlaceable.width
        val maxHeight = characterPlaceable.height + progressPlaceable.height

        layout(maxWidth, maxHeight) {
            val characterXPos = if (progress == 1f)
                (maxWidth * progress).roundToInt() - characterPlaceable.width
            else
                (maxWidth * progress).roundToInt() - characterPlaceable.width / 2
            characterPlaceable.place(
                x = characterXPos,
                y = 0
            )
            progressPlaceable.place(x = 0, y = characterPlaceable.height)
        }
    }
}

/**
 * @param progress 0.0f ~ 1.0f
 */
@Composable
internal fun StepProgress(
    modifier: Modifier = Modifier,
    outLineColor: Color = MaterialTheme.colorScheme.primaryContainer,
    fillColor: Color = MaterialTheme.colorScheme.primary,
    progress: Float,
) {
    Canvas(
        modifier = modifier,
    ) {
        drawRoundRect(
            color = outLineColor,
            style = Fill,
            cornerRadius = CornerRadius(10f)
        )
        drawRoundRect(
            color = fillColor,
            size = Size(
                width = size.width * progress,
                height = size.height
            ),
            style = Fill,
            cornerRadius = CornerRadius(10f)
        )
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