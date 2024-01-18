package jinproject.stepwalk.mission.component

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.progressSemantics
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ProgressIndicatorDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.mission.screen.state.MissionValue

@Composable
internal fun MissionSuccessCircleView(
    modifier: Modifier = Modifier,
    text : String,
    color : Color = StepWalkColor.blue_400.color,
    strokeWidth : Dp = 5.dp,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
){
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = strokeCap)
    }
    Box(
        modifier = modifier,
    ) {
        DescriptionLargeText(
            text = text,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .align(Alignment.Center),
            textAlign = TextAlign.Center
        )
        Canvas(
            Modifier
                .fillMaxSize()
                .progressSemantics(1f)
                .padding(10.dp)
                .align(Alignment.Center)
        ) {
            val diameterOffset = stroke.width / 2
            val arcDimen = size.width - 2 * diameterOffset
            drawArc(
                color = color,
                startAngle = 270f,
                sweepAngle = 360f,
                useCenter = false,
                topLeft = Offset(diameterOffset, diameterOffset),
                size = Size(arcDimen, arcDimen),
                style = stroke
            )
        }

    }
}

@Composable
internal fun AnimatedCircularProgressIndicator(
    text : String,
    missionValue: MissionValue,
    modifier: Modifier = Modifier,
    color: Color = StepWalkColor.blue_400.color,
    strokeWidth: Dp = 6.dp,
    trackColor: Color = MaterialTheme.colorScheme.onSurface,
    strokeCap: StrokeCap = ProgressIndicatorDefaults.CircularDeterminateStrokeCap,
) {
    val animateFloat = remember { Animatable(0f) }
    val stroke = with(LocalDensity.current) {
        Stroke(width = strokeWidth.toPx(), cap = strokeCap)
    }

    LaunchedEffect(key1 = animateFloat){
        animateFloat.animateTo(
            targetValue = missionValue.progress(),
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }
    Box(
        modifier = modifier
    ){
        DescriptionLargeText(
            text = missionValue.max.toString(),
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .padding(top = 5.dp, start = 12.dp, end = 12.dp)
                .border(BorderStroke(2.dp, StepWalkColor.blue_200.color)),
            textAlign = TextAlign.Center
        )
        DescriptionLargeText(
            text = "${missionValue.now}\n$text",
            modifier = Modifier
                .align(Alignment.Center),
            textAlign = TextAlign.Center
        )
        Canvas(
            Modifier
                .progressSemantics()
                .align(Alignment.Center)
                .fillMaxSize()
        ) {
            val startAngle = 320f
            val sweep = animateFloat.value * 260f
            drawCircularProgressIndicator(color = trackColor,stroke = stroke)
            drawCircularProgressIndicator(startAngle, sweep, color, stroke)

            if (missionValue.isMatched()) {
                drawCircularProgressIndicator(color = color,stroke = stroke)//달성시 상자로 보여줌
            }
        }
    }
}

private fun DrawScope.drawCircularProgressIndicator(
    startAngle: Float = 320f,
    sweep: Float = 260f,
    color: Color,
    stroke: Stroke
) {
    val diameterOffset = stroke.width / 2
    val arcDimen = size.width - 2 * diameterOffset
    drawArc(
        color = color,
        startAngle = startAngle,
        sweepAngle = sweep,
        useCenter = false,
        topLeft = Offset(diameterOffset, diameterOffset),
        size = Size(arcDimen, arcDimen),
        style = stroke
    )
}

@Composable
internal fun MissionBar(
    modifier: Modifier = Modifier,
    missionValue: MissionValue,
    textColor : Color,
    progressColor: Color,
    height : Dp = 10.dp,
    trackColor: Color = MaterialTheme.colorScheme.onSurface
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        LinearProgressIndicator(
            progress = missionValue.now/missionValue.max.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(height)
                .clip(RoundedCornerShape(8.dp)),
            color = progressColor,
            trackColor = trackColor,
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            DescriptionSmallText(
                text = "${missionValue.now}/${missionValue.max}",
                color = textColor
            )
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewCircle(

) = StepWalkTheme {
    MissionSuccessCircleView(
        text = "10000",
        color = StepWalkColor.blue_400.color,
        modifier = Modifier.size(150.dp)
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewLinear(

) = StepWalkTheme {
    MissionBar(
        missionValue = MissionValue(),
        textColor = StepWalkColor.blue_400.color,
        progressColor = Color.Green
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
private fun PreviewCir(

) = StepWalkTheme {
    AnimatedCircularProgressIndicator(
        text = "걸음수",
        missionValue = MissionValue(1000,30000),
        modifier = Modifier.size(200.dp)
    )
}