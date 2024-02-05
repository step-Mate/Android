package jinproject.stepwalk.mission.screen.mission.component

import androidx.annotation.DrawableRes
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathOperation
import androidx.compose.ui.graphics.SolidColor
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
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.DefaultIconButton
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.domain.model.MissionCommon
import jinproject.stepwalk.domain.model.MissionList
import jinproject.stepwalk.domain.model.MissionMode

@Composable
internal fun MissionItem(
    modifier: Modifier = Modifier,
    missionList: MissionList,
    onClick : (MissionMode) -> Unit
){
    Column(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 10.dp, start = 15.dp, end = 15.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DescriptionLargeText(
                text = missionList.title,
                color = MaterialTheme.colorScheme.onSurface
            )
            DefaultIconButton(
                icon = R.drawable.ic_arrow_right_small,
                onClick = {onClick(missionList.mode)},
                iconTint = MaterialTheme.colorScheme.onSurface
            )
        }
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 15.dp, end = 5.dp, bottom = 20.dp),
            state = rememberLazyListState(),
            horizontalArrangement = Arrangement.spacedBy(if (missionList.mode == MissionMode.repeat) 5.dp else 15.dp),
            verticalAlignment = Alignment.CenterVertically
        ){
            items(items = missionList.list, key = { it.designation }){mission ->
                if (missionList.mode == MissionMode.repeat){
                    MissionMedal(
                        modifier = Modifier.size(100.dp),
                        icon = missionList.icon,
                        mission = mission,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }else{
                    MissionBadge(
                        modifier = Modifier.size(90.dp),
                        icon = missionList.icon,
                        mission = mission,
                        color = MaterialTheme.colorScheme.primary,
                        )
                }
            }
        }
    }
}

@Composable
internal fun MissionMedal(
    modifier: Modifier,
    @DrawableRes icon : Int,
    mission : MissionCommon,
    textStyle : TextStyle = MaterialTheme.typography.titleSmall,
    color: Color,
    backgroundColor : Color = MaterialTheme.colorScheme.background,
    containerColor: Color = MaterialTheme.colorScheme.scrim,
    trackColor: Color = MaterialTheme.colorScheme.onSurface
){
    val vector = ImageVector.vectorResource(id = icon)
    val painter = rememberVectorPainter(image = vector)
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(mission.getMissionGoal().toString(),textStyle) {
        textMeasurer.measure(mission.getMissionGoal().toString(),textStyle)
    }
    val animateFloat = remember { Animatable(0f) }
    LaunchedEffect(key1 = animateFloat){
        animateFloat.animateTo(
            targetValue = mission.getMissionProgress(),
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
    ){
        Canvas(modifier = Modifier.fillMaxSize()){
            val width = size.width
            val height = size.height

            rotate(15f){
                drawPath(
                    path = missionFlagpole(offset = Offset(width * 0.3f,height*0.6f), size = Size(width * 0.2f,height* 0.25f)),
                    brush = SolidColor(containerColor),
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
            rotate(-15f){
                drawPath(
                    path = missionFlagpole(offset = Offset(width * 0.5f,height*0.6f), size = Size(width * 0.2f,height* 0.35f)),
                    brush = SolidColor(containerColor),
                    style = Stroke(width = 2.dp.toPx()),
                )
            }
            drawCircle(
                brush = SolidColor(trackColor),
                radius = width * 0.33f,
                center = Offset(width* 0.5f,height * 0.35f)
            )
            drawCircle(
                color = containerColor,
                style = Stroke(width = 2.dp.toPx()),
                radius = width * 0.33f,
                center = Offset(width* 0.5f,height * 0.35f)
            )
            drawArc(
                color = color,
                startAngle = 270f,
                sweepAngle = animateFloat.value * 360f,
                useCenter = false,
                topLeft = Offset(width* 0.187f,height * 0.037f),
                size = Size(width * 0.626f,height * 0.626f),
                style = Stroke(width = width * 0.03f),
            )
            drawCircle(
                color = containerColor,
                style = Stroke(width = 2.dp.toPx()),
                radius = width * 0.3f,
                center = Offset(width* 0.5f,height * 0.35f)
            )
            drawCircle(
                brush = SolidColor(backgroundColor),
                radius = width * 0.3f,
                center = Offset(width* 0.5f,height * 0.35f)
            )
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = center.x - textLayoutResult.size.width / 2,
                    y = height * 0.15f,
                )
            )
            translate(left = width* 0.4f, top = height * 0.4f){
                with(painter){
                    draw(size = Size(width* 0.2f,height * 0.2f))
                }
            }
        }
    }
}

private fun missionFlagpole(
    offset: Offset,
    size: Size
) : Path {
    val width = offset.x + size.width
    val height = offset.y + size.height
    return Path().apply {
        moveTo(offset.x,offset.y)
        lineTo(width,offset.y)
        lineTo(width,height)
        lineTo(offset.x + size.width * 0.5f, offset.y + size.height * 0.7f)
        lineTo(offset.x,height)
        lineTo(offset.x,offset.y)
    }
}


@Composable
internal fun MissionBadge(
    modifier: Modifier,
    @DrawableRes icon : Int,
    mission: MissionCommon,
    textStyle : TextStyle = MaterialTheme.typography.titleSmall,
    color: Color,
    backgroundColor : Color = MaterialTheme.colorScheme.background,
    containerColor: Color = MaterialTheme.colorScheme.scrim,
    trackColor: Color = MaterialTheme.colorScheme.onSurface
){
    val vector = ImageVector.vectorResource(id = icon)
    val painter = rememberVectorPainter(image = vector)
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(mission.getMissionGoal().toString(),textStyle) {
        textMeasurer.measure(mission.getMissionGoal().toString(),textStyle)
    }
    val animateFloat = remember { Animatable(0f) }
    LaunchedEffect(key1 = animateFloat){
        animateFloat.animateTo(
            targetValue = mission.getMissionProgress(),
            animationSpec = tween(durationMillis = 1000, easing = FastOutSlowInEasing)
        )
    }

    Box(
        modifier = modifier
    ){
        Canvas(modifier = Modifier.fillMaxSize()){
            val width = size.width
            val height = size.height

            val shield = missionShield(
                offset = Offset(width *0.1f,height *0.2f),
                size = Size(width* 0.8f, height* 0.8f),
                roundConner = 5.dp.toPx()
            )
            val innerShield = missionShield(
                offset = Offset(width * 0.15f,height * 0.25f),
                size = Size(width* 0.7f, height* 0.7f),
                roundConner = 5.dp.toPx()
            )
            clipPath(
                path = shield,
                clipOp = ClipOp.Intersect
            ){
                drawPath(
                    path = shield,
                    brush = SolidColor(trackColor)
                )
                drawArc(
                    color = color,
                    startAngle = 270f,
                    sweepAngle = animateFloat.value * 360f,
                    useCenter = false,
                    topLeft = Offset(0f,0f),
                    size = Size(width,height),
                    style = Stroke(width = width * 0.5f),
                )
            }
            drawPath(
                path = innerShield,
                brush = SolidColor(containerColor),
                style = Stroke(width = 2.dp.toPx()),
            )
            drawPath(
                path = innerShield,
                brush = SolidColor(backgroundColor),
            )
            drawPath(
                path = shield,
                brush = SolidColor(containerColor),
                style = Stroke(width = 1.dp.toPx()),
            )
            drawText(
                textLayoutResult = textLayoutResult,
                topLeft = Offset(
                    x = center.x - textLayoutResult.size.width / 2,
                    y = height * 0.35f,
                )
            )
            translate(left = width* 0.4f, top = height * 0.6f){
                with(painter){
                    draw(size = Size(width* 0.25f,height * 0.25f))
                }
            }
        }
    }
}

private fun missionShield(
    offset: Offset,
    size: Size,
    roundConner : Float
) : Path {
    val width = size.width
    val height = size.height
    val rectPath = Path().apply {
        addRoundRect(
            RoundRect(
                Rect(
                    offset = Offset(offset.x,offset.y),
                    size = Size(width,height * 0.5f)
                ),
                topLeft = CornerRadius(roundConner),
                topRight = CornerRadius(roundConner),
            )
        )
    }
    val arcPath = Path().apply {
        addOval(
            Rect(
                offset = Offset(offset.x,offset.y),
                size = Size(width, height)
            )
        )
    }
    return Path().apply {
        op(rectPath,arcPath, PathOperation.Union)
    }
}

@Composable
@Preview(widthDp = 100)
private fun PreviewMissionBadge(

) = StepWalkTheme {
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

) = StepWalkTheme {
//    MissionMedal(
//        modifier = Modifier.size(100.dp),
//        icon = R.drawable.ic_fire,
//        text = "100",
//        color = Color.Yellow,
//        containerColor = MaterialTheme.colorScheme.scrim
//    )
}
