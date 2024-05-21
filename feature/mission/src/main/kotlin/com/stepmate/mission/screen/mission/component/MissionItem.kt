package com.stepmate.mission.screen.mission.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.unit.dp
import com.stepmate.design.R
import com.stepmate.design.component.DescriptionLargeText
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.domain.model.mission.MissionCommon
import com.stepmate.domain.model.mission.MissionComposite
import com.stepmate.mission.screen.component.MissionBadge
import com.stepmate.mission.screen.component.MissionMedal
import com.stepmate.mission.util.getIcon

@Composable
internal fun MissionItem(
    modifier: Modifier = Modifier,
    missionList: List<MissionCommon>,
    onClick: () -> Unit
) {
    var designation by remember { mutableStateOf("") }
    LaunchedEffect(key1 = missionList) {
        designation =
            missionList.find { it.getMissionAchieved() < it.getMissionGoal() }?.designation
                ?: ""
    }
    Column(
        modifier = modifier
            .shadow(elevation = 6.dp, RoundedCornerShape(8.dp))
            .background(MaterialTheme.colorScheme.surface)
            .fillMaxWidth()
            .clickable { onClick() },
    ) {
        Row(
            modifier = Modifier
                .padding(top = 20.dp, start = 20.dp, end = 20.dp)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            DescriptionLargeText(
                text = missionList.first().getMissionTitle(),
                color = MaterialTheme.colorScheme.onSurface
            )
            Icon(
                imageVector = ImageVector.vectorResource(id = R.drawable.ic_arrow_right_small),
                contentDescription = "right"
            )
        }
        VerticalSpacer(height = if (missionList.first() is MissionComposite) 0.dp else 20.dp)
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(
                    start = if (missionList.first() is MissionComposite) 10.dp else 5.dp,
                    end = 10.dp,
                    bottom = if (missionList.first() is MissionComposite) 20.dp else 10.dp,
                ),
            state = rememberLazyListState(),
            horizontalArrangement = Arrangement.spacedBy(if (missionList.first() is MissionComposite) 10.dp else 0.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (missionList.first() is MissionComposite) {//통합미션
                items(items = missionList, key = { it.designation }) { mission ->
                    MissionBadge(
                        modifier = Modifier.size(100.dp),
                        icon = mission.getIcon(),
                        mission = mission,
                        animate = mission.designation == designation,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            } else {//목표미션
                items(items = missionList, key = { it.designation }) { mission ->
                    MissionMedal(
                        modifier = Modifier.size(110.dp),
                        icon = mission.getIcon(),
                        mission = mission,
                        animate = mission.designation == designation,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }
            }

        }
    }
}



