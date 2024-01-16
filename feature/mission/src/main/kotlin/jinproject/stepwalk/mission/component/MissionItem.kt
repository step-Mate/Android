package jinproject.stepwalk.mission.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.mission.screen.state.Mission

@Composable
internal fun MissionItem(
    mission: Mission,
    onClick : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(mission.contentColor)
            .clickable { onClick() },
    ) {
        VerticalSpacer(height = 10.dp)
        Text(
            modifier = Modifier.padding(horizontal = 12.dp),
            text = mission.title,
            style = MaterialTheme.typography.titleLarge,
            color = mission.containerColor
        )
        VerticalSpacer(height = 20.dp)
        MissionBar(
            modifier = Modifier.padding(bottom = 12.dp),
            nowValue = mission.missionValue.nowValue,
            maxValue = mission.missionValue.maxValue,
            textColor = mission.containerColor,
            progressColor = mission.containerColor
        )
    }
}



@Composable
@Preview(widthDp = 150)
private fun PreviewMissionItem(

) = StepWalkTheme {
    MissionItem(
        mission = Mission(""),
        onClick = {}
    )
}