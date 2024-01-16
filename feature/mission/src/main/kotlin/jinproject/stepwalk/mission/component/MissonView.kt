package jinproject.stepwalk.mission.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.HeadlineText
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme

@Composable
internal fun MissonList(

) {


}

@Composable
internal fun MissionCircleView(
    modifier: Modifier = Modifier,
    text : String,
    progress: Float,
    progressColor : Color ,
    trackColor : Color = MaterialTheme.colorScheme.onSurface,
    strokeWidth : Dp = 8.dp
){
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.padding(start = 8.dp),
            color = progressColor,
            trackColor = trackColor,
            strokeWidth = strokeWidth
        )
        HeadlineText(
            text = text,
            modifier = Modifier
                .weight(0.8f)
                .padding(horizontal = 12.dp)
        )
    }
}

@Composable
internal fun MissionBarView(
    modifier: Modifier = Modifier,
    missonText : String,
    progressText : String,
    progress: Float,
    backgroundColor : Color,
    textColor : Color,
    progressColor: Color,
    trackColor: Color = MaterialTheme.colorScheme.onSurface
){
    Column(
        modifier = modifier
            .padding(vertical = 8.dp, horizontal = 12.dp)
            .clip(RoundedCornerShape(8.dp))
            .background(backgroundColor),
        verticalArrangement = Arrangement.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DescriptionLargeText(
                modifier = Modifier.weight(0.8f),
                text = missonText,
                color = textColor
            )
            DescriptionSmallText(
                text = progressText,
                color = textColor
            )
        }
        LinearProgressIndicator(
            progress = progress,
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 5.dp, horizontal = 12.dp)
                .height(20.dp)
                .clip(RoundedCornerShape(8.dp))
            ,
            color = progressColor,
            trackColor = trackColor,
        )
    }
}

@Composable
internal fun MissionBar(
    modifier: Modifier = Modifier,
    nowValue : Int,
    maxValue : Int,
    textColor : Color,
    progressColor: Color,
    trackColor: Color = MaterialTheme.colorScheme.onSurface
){
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center
    ) {
        LinearProgressIndicator(
            progress = nowValue/maxValue.toFloat(),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp)
                .height(10.dp)
                .clip(RoundedCornerShape(8.dp)),
            color = progressColor,
            trackColor = trackColor,
        )
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 12.dp),
            horizontalArrangement = Arrangement.End
        ) {
            DescriptionSmallText(
                text = "$nowValue/$maxValue",
                color = textColor
            )
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewCircle(

) = StepWalkTheme {
    MissionCircleView(
        text = "test1111111111111",
        progress = 0.75f,
        progressColor = Color.Green
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewLinear(

) = StepWalkTheme {
    MissionBarView(
        missonText = "test111111111111122222222222",
        progressText = "123/235",
        progress = 0.23f,
        backgroundColor = StepWalkColor.blue_200.color,
        textColor = StepWalkColor.blue_400.color,
        progressColor = Color.Green
    )
}