package jinproject.stepwalk.mission.component

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
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.mission.screen.state.Mission
import jinproject.stepwalk.mission.screen.state.MissionDetail
import jinproject.stepwalk.mission.screen.state.MissionList
import jinproject.stepwalk.mission.screen.state.MissionValue

@Composable
internal fun MissionItem(
    mission: Mission,
    onClick : () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(mission.title.contentColor)
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
                .padding(start = 15.dp, top=25.dp),
            text = mission.title.title,
            style = MaterialTheme.typography.titleLarge,
            color = mission.title.containerColor
        )
        MissionBar(
            modifier = Modifier.padding(bottom = 6.dp, top = 10.dp),
            missionValue = mission.value,
            textColor = mission.title.containerColor,
            progressColor = mission.title.containerColor
        )
    }
}

@Composable
internal fun MissionBarItem(
    modifier: Modifier = Modifier,
    missionDetail: MissionDetail,
    containerColor : Color = StepWalkColor.blue_200.color,
    contantColor : Color = StepWalkColor.blue_400.color
){
    Column(
        modifier = modifier
            .background(containerColor, RoundedCornerShape(5.dp)),
    ) {
        DescriptionLargeText(
            text = missionDetail.title,
            modifier = Modifier.padding(horizontal = 15.dp, vertical = 10.dp),
            color = contantColor
        )
        MissionBar(
            missionValue = missionDetail.value,
            textColor = contantColor,
            progressColor = contantColor,
            modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
        )
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