package com.stepmate.mission.screen.missiondetail.component

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.stepmate.design.R
import com.stepmate.design.component.DefaultIconButton
import com.stepmate.design.component.DescriptionLargeText
import com.stepmate.design.component.DescriptionSmallText
import com.stepmate.design.component.HeadlineText
import com.stepmate.domain.model.mission.MissionCommon
import com.stepmate.domain.model.mission.MissionComposite
import com.stepmate.mission.screen.component.MissionBadge
import com.stepmate.mission.screen.component.MissionMedal
import com.stepmate.mission.util.getIcon
import com.stepmate.mission.util.getString

@Composable
internal fun MissionCompositeView(
    modifier: Modifier = Modifier,
    selectMission: MissionComposite,
    designation: String,
) {
    var detailMission by remember { mutableStateOf(false) }
    MissionBadge(
        modifier = modifier.size(200.dp),
        icon = selectMission.getIcon(),
        mission = selectMission,
        animate = selectMission.designation == designation,
        textStyle = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
    )
    Row(
        modifier = Modifier
            .wrapContentWidth()
            .padding(top = 20.dp)
            .clickable { detailMission = detailMission.not() },
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DescriptionLargeText(
            modifier = Modifier.padding(start = 50.dp),
            text = "${if (selectMission.getMissionProgress() == 1f) selectMission.getMissionGoal() else selectMission.getMissionAchieved()}/${selectMission.getMissionGoal()}",
            textAlign = TextAlign.Center
        )
        DefaultIconButton(
            icon = R.drawable.ic_arrow_down_small,
            onClick = { detailMission = detailMission.not() },
            enabled = true,
            iconTint = MaterialTheme.colorScheme.onSurface,
            backgroundTint = MaterialTheme.colorScheme.background
        )
    }

    AnimatedVisibility(
        visible = detailMission
    ) {
        LazyRow(
            modifier = modifier
                .fillMaxWidth()
                .padding(horizontal = 60.dp, vertical = 10.dp),
            state = rememberLazyListState(),
            horizontalArrangement = Arrangement.spacedBy(30.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            items(
                items = selectMission.missions,
                key = { it.hashCode() }) { mission ->
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    DescriptionLargeText(
                        text = mission.getString(),
                        textAlign = TextAlign.Center
                    )
                    DescriptionSmallText(
                        modifier = Modifier.padding(top = 10.dp),
                        text = "${if(mission.getMissionProgress() == 1f) mission.getMissionGoal() else mission.getMissionAchieved()}/${mission.getMissionGoal()}",
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
    DescriptionLargeText(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        text = selectMission.designation,
        textAlign = TextAlign.Center
    )
    DescriptionSmallText(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        text = selectMission.intro,
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun MissionCommonView(
    modifier: Modifier = Modifier,
    selectMission: MissionCommon,
    designation: String,
) {
    MissionMedal(
        modifier = modifier
            .padding(top = 30.dp)
            .size(200.dp),
        icon = selectMission.getIcon(),
        mission = selectMission,
        animate = selectMission.designation == designation,
        textStyle = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
    )
    DescriptionLargeText(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 20.dp),
        text = "${if(selectMission.getMissionProgress() == 1f) selectMission.getMissionGoal() else selectMission.getMissionAchieved()}/${selectMission.getMissionGoal()}",
        textAlign = TextAlign.Center
    )
    HeadlineText(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 30.dp),
        text = selectMission.designation,
        textAlign = TextAlign.Center
    )
    DescriptionLargeText(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        text = selectMission.intro,
        textAlign = TextAlign.Center
    )
}