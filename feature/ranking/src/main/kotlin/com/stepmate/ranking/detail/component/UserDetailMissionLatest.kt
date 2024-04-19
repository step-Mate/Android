package com.stepmate.ranking.detail.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.key
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.stepmate.design.component.DescriptionLargeText
import com.stepmate.design.component.DescriptionSmallText
import com.stepmate.design.component.FooterText
import com.stepmate.design.component.HorizontalWeightSpacer
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.domain.model.mission.MissionComponent
import com.stepmate.ranking.detail.User
import com.stepmate.ranking.detail.UserDetailPreviewParameter

@Composable
internal fun MissionLatest(
    missions: List<MissionComponent>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .padding(10.dp),
    ) {
        DescriptionLargeText(
            text = "진행중인 미션",
            modifier = Modifier.padding(vertical = 10.dp)
        )
        if (missions.isEmpty()) {
            VerticalSpacer(height = 16.dp)
            DescriptionSmallText(
                text = "진행중인 미션이 존재하지 않아요.",
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentWidth(),
            )
        }
        else
            missions.forEachIndexed { index, mission ->
                key(mission.getMissionDesignation()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.outline,
                                RoundedCornerShape(10.dp)
                            )
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        DescriptionSmallText(text = mission.getMissionDesignation())
                        HorizontalWeightSpacer(float = 1f)
                        FooterText(text = "${mission.getMissionAchieved()} / ${mission.getMissionGoal()}")
                    }
                }
                if (index != missions.lastIndex)
                    VerticalSpacer(height = 8.dp)
            }
    }
}

@Composable
@Preview
private fun PreviewMissionLatest(
    @PreviewParameter(UserDetailPreviewParameter::class)
    user: User,
) = StepMateTheme {
    MissionLatest(missions = user.latestMissions)
}