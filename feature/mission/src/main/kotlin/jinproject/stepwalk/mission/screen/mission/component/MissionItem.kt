package jinproject.stepwalk.mission.screen.mission.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.domain.model.mission.MissionComposite
import jinproject.stepwalk.domain.model.mission.MissionList
import jinproject.stepwalk.mission.screen.component.MissionBadge
import jinproject.stepwalk.mission.screen.component.MissionMedal
import jinproject.stepwalk.mission.util.getIcon

@Composable
internal fun MissionItem(
    modifier: Modifier = Modifier,
    missionList: MissionList,
    onClick: () -> Unit
) {
    Column(
        modifier = modifier
            .shadow(elevation = 6.dp, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        DescriptionLargeText(
            modifier = Modifier
                .padding(top = 20.dp, start = 20.dp),
            text = missionList.title,
            color = MaterialTheme.colorScheme.onSurface
        )
        VerticalSpacer(height = if (missionList.list.first() is MissionComposite) 0.dp else 20.dp)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (missionList.list.first() is MissionComposite) 10.dp else 5.dp,
                    end = 10.dp,
                    bottom = if (missionList.list.first() is MissionComposite) 20.dp else 10.dp,
                ),
            state = rememberLazyListState(),
            horizontalArrangement = Arrangement.spacedBy(if (missionList.list.first() is MissionComposite) 10.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (missionList.list.first() is MissionComposite) {//통합미션
                items(items = missionList.list, key = { it.designation }) { mission ->
                    MissionBadge(
                        modifier = Modifier.size(100.dp),
                        icon = mission.getIcon(),
                        mission = mission,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            } else {//목표미션
                items(items = missionList.list, key = { it.designation }) { mission ->
                    MissionMedal(
                        modifier = Modifier.size(110.dp),
                        icon = mission.getIcon(),
                        mission = mission,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

        }
    }
}



