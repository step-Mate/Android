package jinproject.stepwalk.mission.screen.missiondetail.missonrepeat

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.HeadlineText
import jinproject.stepwalk.design.component.StepMateBoxDefaultTopBar
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.domain.model.MissionList
import jinproject.stepwalk.domain.model.MissionMode
import jinproject.stepwalk.domain.model.StepMission
import jinproject.stepwalk.mission.screen.mission.component.MissionBadge
import jinproject.stepwalk.mission.screen.mission.component.MissionMedal

@Composable
internal fun MissionRepeatScreen(
    missionDetailViewModel: MissionDetailViewModel = hiltViewModel(),
    popBackStack: () -> Unit
) {
    MissionRepeatScreen(
        title = missionDetailViewModel.title,
        detailList = missionDetailViewModel.list,
        popBackStack = popBackStack
    )
}

@Composable
private fun MissionRepeatScreen(
    title : String,
    detailList : MissionList,
    popBackStack: () -> Unit
){
    DefaultLayout(
        contentPaddingValues = PaddingValues(),
        topBar = {
            StepMateBoxDefaultTopBar(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.surface)
                    .windowInsetsPadding(WindowInsets.statusBars),
                icon = R.drawable.ic_arrow_left_small,
                onClick = popBackStack
            ) {
                HeadlineText(text = title, modifier = Modifier.align(Alignment.Center))
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.4f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (detailList.mode == MissionMode.repeat){
                MissionMedal(
                    modifier = Modifier.size(200.dp),
                    icon = detailList.icon,
                    mission = detailList.list[0],
                    textStyle = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }else{
                MissionBadge(
                    modifier = Modifier.size(200.dp),
                    icon = detailList.icon,
                    mission = detailList.list[0],
                    textStyle = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
            }
        }

        Column(
            modifier = Modifier
                .weight(0.6f)
                .shadow(
                    elevation = 8.dp,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
        ) {
            VerticalSpacer(height = 20.dp)
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                modifier = Modifier
                    .fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(if (detailList.mode == MissionMode.repeat) 10.dp else 20.dp),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
            ){
                items(detailList.list, key = { it.designation }){ mission ->
                    if (detailList.mode == MissionMode.repeat){
                        MissionMedal(
                            modifier = Modifier.size(110.dp),
                            icon = detailList.icon,
                            mission = mission,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }else{
                        MissionBadge(
                            modifier = Modifier.size(110.dp),
                            icon = detailList.icon,
                            mission = mission,
                            color = MaterialTheme.colorScheme.primary,
                        )
                    }
                }
            }
        }


    }
}

@Composable
@Preview
private fun PreviewMissionRepeatScreen(

) = StepWalkTheme {
    MissionRepeatScreen(
        title = "칼로리",
        detailList = MissionList(
            title = "일일 미션",
            icon = R.drawable.ic_fire,
            mode = MissionMode.repeat,
            list = listOf(
                StepMission(
                    designation = "100 심박수",
                    intro = "100걸음을 달성",
                    achieved = 10,
                    goal = 100
                ),
                StepMission(
                    designation = "200",
                    intro = "",
                    achieved = 10,
                    goal = 100
                ),
                StepMission(
                    designation = "300",
                    intro = "",
                    achieved = 10,
                    goal = 100
                ),
                StepMission(
                    designation = "400",
                    intro = "",
                    achieved = 10,
                    goal = 100
                ),
            )
        ),
        popBackStack = {}
    )
}

