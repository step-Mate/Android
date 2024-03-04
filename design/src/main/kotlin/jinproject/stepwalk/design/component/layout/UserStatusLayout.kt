package jinproject.stepwalk.design.component.layout

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.FooterText
import jinproject.stepwalk.design.theme.StepWalkTheme
import kotlin.math.roundToInt

@Composable
fun StepLayout(
    modifier: Modifier = Modifier,
    characterContent: @Composable () -> Unit,
    progressContent: @Composable (Float) -> Unit,
    progress: Float,
) {
    val progressComposable = @Composable {
        progressContent(progress)
    }

    Layout(
        contents = listOf(
            characterContent,
            progressComposable,
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

@Composable
fun StepFooterLayout(
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

/**
 * @param progress 0.0f ~ 1.0f
 */
@Composable
fun StepProgress(
    modifier: Modifier = Modifier,
    outLineColor: Color = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
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
private fun PreviewStepLayout() = StepWalkTheme {
    StepLayout(
        modifier = Modifier.fillMaxWidth(),
        characterContent = {
            Icon(
                painter = painterResource(id = R.drawable.ic_person_walking),
                contentDescription = null
            )
        },
        progressContent = { progress ->
            StepProgress(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                progress = progress
            )
        },
        progress = 0.5f
    )
}

@Composable
@Preview(showBackground = true)
private fun PreviewStepFooterLayout() = StepWalkTheme {
    val step = 500
    val goal = 1000
    val progress = step.toFloat() / goal

    StepFooterLayout(
        totalContent = {
            FooterText(
                text = step.toString(),
            )
        },
        goalContent = {
            FooterText(text = goal.toString())
        },
        separatorContent = {
            FooterText(text = " / ")
        },
        progress = progress,
    )
}