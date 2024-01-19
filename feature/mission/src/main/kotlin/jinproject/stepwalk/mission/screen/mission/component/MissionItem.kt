package jinproject.stepwalk.mission.screen.mission.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.mission.screen.missiondetail.component.MissionBar
import jinproject.stepwalk.mission.screen.mission.state.Mission
import jinproject.stepwalk.mission.screen.mission.state.MissionList
import jinproject.stepwalk.mission.screen.mission.state.MissionMode
import jinproject.stepwalk.mission.screen.mission.state.MissionValue

@Composable
internal fun MissionItem(
    mission: Mission,
    contentColor: Color = StepWalkColor.blue_400.color,
    containerColor: Color = StepWalkColor.blue_200.color,
    onClick : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(containerColor)
            .clickable { onClick() },
    ) {
        Image(
            imageVector = ImageVector.vectorResource(mission.title.image),
            contentDescription = "미션 이미지",
            modifier = Modifier
                .size(130.dp)
                .padding(top = 20.dp)
                .align(Alignment.CenterHorizontally),
        )
        Text(
            modifier = Modifier
                .padding(top=25.dp, bottom = 10.dp)
                .align(Alignment.CenterHorizontally),
            text = mission.title.title,
            style = MaterialTheme.typography.titleLarge,
            color = contentColor
        )
        if (mission.title.mode == MissionMode.time) {
            MissionBar(
                modifier = Modifier.padding(bottom = 6.dp),
                missionValue = mission.value,
                textColor = contentColor,
                progressColor = contentColor
            )
        }
    }
}

@Composable
@Preview(widthDp = 150)
private fun PreviewMissionItem(

) = StepWalkTheme {
    MissionItem(
        mission = Mission(MissionList.list[0], MissionValue()),
        onClick = {}
    )
}

