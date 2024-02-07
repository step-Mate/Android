package jinproject.stepwalk.design.component

import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshState
import androidx.compose.material.pullrefresh.pullRefreshIndicatorTransform
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.pushRefresh.PushRefreshState
import jinproject.stepwalk.design.theme.StepWalkTheme
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun StepMateProgressIndicatorRotating(
    modifier: Modifier = Modifier,
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        contentAlignment = Alignment.Center
    ) {
        StepMateProgressIndicatorInfiniteRotating()
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun StepMatePullRefreshIndicator(
    modifier: Modifier = Modifier,
    state: PullRefreshState,
    isRefreshing: Boolean,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .size(40.dp)
            .pullRefreshIndicatorTransform(state)
            .background(
                color = MaterialTheme.colorScheme.surface,
            ),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = isRefreshing,
            animationSpec = tween(100),
            label = "CrossFade Refreshing"
        ) { isRefreshing ->
            if (isRefreshing) {
                StepMateProgressIndicatorInfiniteRotating()
            } else {
                StepMateProgressIndicatorRotatingByParam(
                    progress = state.progress,
                )
            }
        }
    }
}

@Composable
fun StepMatePushRefreshIndicator(
    modifier: Modifier = Modifier,
    state: PushRefreshState,
    isRefreshing: Boolean,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(
                if (isRefreshing) {
                    state.maxHeight.dp
                } else
                    (state.progress * state.maxHeight).dp
            )
            .background(
                color = MaterialTheme.colorScheme.surface,
            ),
        contentAlignment = Alignment.Center
    ) {
        Crossfade(
            targetState = isRefreshing,
            animationSpec = tween(100),
            label = "CrossFade Refreshing"
        ) { isRefreshing ->
            if (isRefreshing) {
                StepMateProgressIndicatorInfiniteRotating()
            } else {
                StepMateProgressIndicatorRotatingByParam(state.progress)
            }
        }
    }
}

@Composable
private fun StepMateProgressIndicatorRotatingByParam(
    progress: Float,
    modifier: Modifier = Modifier,
    counter: Int = 8,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    Canvas(modifier = modifier) {

        val stroke = Stroke(
            width = 1.dp.toPx(),
        )

        val offset = StepMateProgressIndicatorOffset(
            centerOffset = Offset(center.x, center.y),
            radius = 10.dp.toPx()
        )

        val path = Path().apply {
            for (i in 1..(progress * counter).toInt()) {
                moveTo(
                    offset.getPoint(ceta = 360.0 / counter * i).x,
                    offset.getPoint(ceta = 360.0 / counter * i).y
                )
                lineTo(
                    offset.getPoint(ceta = 360.0 / counter * i).x / 2f,
                    offset.getPoint(ceta = 360.0 / counter * i).y / 2f
                )
            }
        }

        drawPath(path, color = color, style = stroke)
    }
}

@Composable
private fun StepMateProgressIndicatorInfiniteRotating(
    modifier: Modifier = Modifier,
    counter: Int = 8,
    color: Color = MaterialTheme.colorScheme.onSurface,
) {
    val infiniteTransition = rememberInfiniteTransition(label = "infinite Transition")
    val rotationAnimation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 8f,
        animationSpec = infiniteRepeatable(
            tween(8 * 80, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotation Animation"
    )

    Canvas(modifier = modifier) {

        val stroke = Stroke(
            width = 1.dp.toPx(),
        )

        val offset = StepMateProgressIndicatorOffset(
            centerOffset = Offset(center.x, center.y),
            radius = 10.dp.toPx()
        )

        val path = Path().apply {
            for (i in 1..counter) {
                moveTo(
                    offset.getPoint(ceta = 360.0 / counter * i).x,
                    offset.getPoint(ceta = 360.0 / counter * i).y
                )
                lineTo(
                    offset.getPoint(ceta = 360.0 / counter * i).x / 2f,
                    offset.getPoint(ceta = 360.0 / counter * i).y / 2f
                )
            }
            close()
        }

        drawPath(path, color = color, style = stroke, alpha = 0.2f)

        val rotatePath = Path().apply {
            moveTo(
                offset.getPoint(ceta = 360.0 / counter).x,
                offset.getPoint(ceta = 360.0 / counter).y
            )
            lineTo(
                offset.getPoint(ceta = 360.0 / counter).x / 2f,
                offset.getPoint(ceta = 360.0 / counter).y / 2f
            )
        }

        for (i in 1..4) {
            rotate(degrees = (rotationAnimation.toInt() + i) * (360f / counter)) {
                drawPath(
                    rotatePath,
                    color = color,
                    style = stroke,
                    alpha = (0.2f + 0.2f * i).coerceIn(0f, 1f)
                )
            }
        }
    }
}

private class StepMateProgressIndicatorOffset(
    private val centerOffset: Offset,
    private val radius: Float,
) {
    fun getPoint(ceta: Double): Offset {
        return Offset(
            x = centerOffset.x + radius * cos(Math.toRadians(ceta).toFloat()),
            y = centerOffset.y + radius * sin(Math.toRadians(ceta).toFloat())
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewStepMateProgressIndicatorInfiniteRotating() = StepWalkTheme {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        contentAlignment = Alignment.Center
    ) {
        StepMateProgressIndicatorInfiniteRotating()
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewStepMateProgressIndicatorRotatingByParam() = StepWalkTheme {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(320.dp),
        contentAlignment = Alignment.Center
    ) {
        StepMateProgressIndicatorRotatingByParam(
            0.5f
        )
    }
}