package com.stepmate.mission.screen.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.graphics.drawscope.rotate
import androidx.compose.ui.graphics.drawscope.translate
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.domain.model.mission.MissionComposite
import com.stepmate.domain.model.mission.MissionFigure

@Composable
internal fun MissionMedal(
    modifier: Modifier,
    @DrawableRes icon: Int,
    mission: MissionFigure,
    animate : Boolean,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
    color: Color,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    containerColor: Color = MaterialTheme.colorScheme.scrim,
    trackColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: ((MissionFigure) -> Unit)? = null
) {
    val vector = ImageVector.vectorResource(id = icon)
    val painter = rememberVectorPainter(image = vector)
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(mission.getMissionGoal().toString(), textStyle) {
        textMeasurer.measure(mission.getMissionGoal().toString(), textStyle)
    }
    val animateFloat =
        remember(mission) { Animatable(if (mission.getMissionAchieved() >= mission.getMissionGoal()) 1f else 0f) }
    LaunchedEffect(key1 = mission,animate) {
        if ((mission.getMissionAchieved() > 0 || mission.getMissionAchieved() < mission.getMissionGoal()) && animate) {
            animateFloat.animateTo(
                targetValue = mission.getMissionProgress(),
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )
        }
    }
    val currentBackgroundColor: Color = when {
        mission.getMissionAchieved() >= mission.getMissionGoal() -> MaterialTheme.colorScheme.primary
        !animate -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        else -> backgroundColor
    }

    Box(
        modifier = modifier.clickable(
            enabled = onClick != null
        ) {
            if (onClick != null) {
                onClick(mission)
            }
        }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            rotate(15f) {
                drawPath(
                    path = missionFlagpole(
                        offset = Offset(width * 0.3f, height * 0.6f),
                        size = Size(width * 0.2f, height * 0.25f)
                    ),
                    color = currentBackgroundColor,
                )
                drawPath(
                    path = missionFlagpole(
                        offset = Offset(width * 0.3f, height * 0.6f),
                        size = Size(width * 0.2f, height * 0.25f)
                    ),
                    color = containerColor,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
            rotate(-15f) {
                drawPath(
                    path = missionFlagpole(
                        offset = Offset(width * 0.5f, height * 0.6f),
                        size = Size(width * 0.2f, height * 0.35f)
                    ),
                    color = currentBackgroundColor,
                )
                drawPath(
                    path = missionFlagpole(
                        offset = Offset(width * 0.5f, height * 0.6f),
                        size = Size(width * 0.2f, height * 0.35f)
                    ),
                    color = containerColor,
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
            drawCircle(
                color = trackColor,
                radius = width * 0.33f,
                center = Offset(width * 0.5f, height * 0.35f)
            )
            drawCircle(
                color = containerColor,
                style = Stroke(width = 2.dp.toPx()),
                radius = width * 0.33f,
                center = Offset(width * 0.5f, height * 0.35f)
            )
            drawArc(
                color = color,
                startAngle = 270f,
                sweepAngle = animateFloat.value * 360f,
                useCenter = false,
                topLeft = Offset(width * 0.187f, height * 0.037f),
                size = Size(width * 0.626f, height * 0.626f),
                style = Stroke(width = width * 0.03f),
            )
            drawCircle(
                color = containerColor,
                style = Stroke(width = 2.dp.toPx()),
                radius = width * 0.3f,
                center = Offset(width * 0.5f, height * 0.35f)
            )
            drawCircle(
                color = currentBackgroundColor,
                radius = width * 0.3f,
                center = Offset(width * 0.5f, height * 0.35f)
            )
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = center.x - textLayoutResult.size.width / 2,
                    y = height * 0.2f,
                )
            )
            translate(left = width * 0.4f, top = height * 0.4f) {
                with(painter) {
                    draw(size = Size(width * 0.2f, height * 0.2f))
                }
            }
        }
    }
}

@Composable
internal fun MissionBadge(
    modifier: Modifier,
    @DrawableRes icon: Int,
    mission: MissionFigure,
    animate : Boolean,
    textStyle: TextStyle = MaterialTheme.typography.bodySmall,
    color: Color,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    containerColor: Color = MaterialTheme.colorScheme.scrim,
    trackColor: Color = MaterialTheme.colorScheme.onSurfaceVariant,
    onClick: ((MissionFigure) -> Unit)? = null
) {
    val vector = ImageVector.vectorResource(id = icon)
    val painter = rememberVectorPainter(image = vector)
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(
        if (mission is MissionComposite) {
            mission.getOriginalGoal().toString()
        } else mission.getMissionGoal().toString(), textStyle
    ) {
        textMeasurer.measure(
            if (mission is MissionComposite) {
                mission.getOriginalGoal().toString()
            } else mission.getMissionGoal().toString(), textStyle
        )
    }
    val animateFloat =
        remember { Animatable(if (mission.getMissionAchieved() >= mission.getMissionGoal()) 1f else 0f) }
    LaunchedEffect(key1 = mission,animate) {
        if ((mission.getMissionAchieved() > 0 || mission.getMissionAchieved() < mission.getMissionGoal()) && animate) {
            animateFloat.animateTo(
                targetValue = mission.getMissionProgress(),
                animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
            )
        }
    }
    val currentBackgroundColor: Color = when {
        mission.getMissionAchieved() >= mission.getMissionGoal() -> MaterialTheme.colorScheme.primary
        !animate -> MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
        else -> backgroundColor
    }

    Box(
        modifier = modifier.clickable(
            enabled = onClick != null
        ) {
            if (onClick != null) {
                onClick(mission)
            }

        }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val width = size.width
            val height = size.height

            val shield = missionShield(
                offset = Offset(width * 0.1f, height * 0.2f),
                size = Size(width * 0.8f, height * 0.8f),
                roundConner = 5.dp.toPx()
            )
            val innerShield = missionShield(
                offset = Offset(width * 0.15f, height * 0.25f),
                size = Size(width * 0.7f, height * 0.7f),
                roundConner = 5.dp.toPx()
            )
            clipPath(
                path = shield,
                clipOp = ClipOp.Intersect
            ) {
                drawPath(
                    path = shield,
                    color = trackColor
                )
                drawArc(
                    color = color,
                    startAngle = 270f,
                    sweepAngle = animateFloat.value * 360f,
                    useCenter = false,
                    topLeft = Offset(0f, 0f),
                    size = Size(width, height),
                    style = Stroke(width = width * 0.5f),
                )
            }
            drawPath(
                path = innerShield,
                color = containerColor,
                style = Stroke(width = 2.dp.toPx()),
            )
            drawPath(
                path = innerShield,
                color = currentBackgroundColor,
            )
            drawPath(
                path = shield,
                color = containerColor,
                style = Stroke(width = 1.dp.toPx()),
            )
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = center.x - textLayoutResult.size.width / 2,
                    y = height * 0.4f,
                )
            )
            translate(left = width * 0.375f, top = height * 0.6f) {
                with(painter) {
                    draw(size = Size(width * 0.25f, height * 0.25f))
                }
            }
        }
    }
}

private fun missionFlagpole(
    offset: Offset,
    size: Size
): Path {
    val width = offset.x + size.width
    val height = offset.y + size.height
    return Path().apply {
        moveTo(offset.x, offset.y)
        lineTo(width, offset.y)
        lineTo(width, height)
        lineTo(offset.x + size.width * 0.5f, offset.y + size.height * 0.7f)
        lineTo(offset.x, height)
        lineTo(offset.x, offset.y)
    }
}

private fun missionShield(
    offset: Offset,
    size: Size,
    roundConner: Float
): Path {
    val width = size.width
    val height = size.height
    val rectPath = Path().apply {
        addRoundRect(
            RoundRect(
                Rect(
                    offset = Offset(offset.x, offset.y),
                    size = Size(width, height * 0.5f)
                ),
                topLeft = CornerRadius(roundConner),
                topRight = CornerRadius(roundConner),
            )
        )
    }
    val arcPath = Path().apply {
        addOval(
            Rect(
                offset = Offset(offset.x, offset.y),
                size = Size(width, height)
            )
        )
    }
    return Path().apply {
        op(rectPath, arcPath, PathOperation.Union)
    }
}

@Composable
@Preview(widthDp = 100)
private fun PreviewMissionBadge(

) = StepMateTheme {
//    MissionBadge(
//        modifier = Modifier.size(100.dp),
//        icon = R.drawable.ic_fire,
//        mission = MissionCommon,
//        color = Color.Yellow,
//        containerColor = MaterialTheme.colorScheme.scrim
//
//    )
}

@Composable
@Preview(widthDp = 100)
private fun PreviewMissionMedal(

) = StepMateTheme {
//    MissionMedal(
//        modifier = Modifier.size(100.dp),
//        icon = R.drawable.ic_fire,
//        text = "100",
//        color = Color.Yellow,
//        containerColor = MaterialTheme.colorScheme.scrim
//    )
}
