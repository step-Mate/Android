package jinproject.stepwalk.mission.screen.missiondetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.HeadlineText
import jinproject.stepwalk.design.component.StepMateBoxDefaultTopBar
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.domain.model.mission.MissionCommon
import jinproject.stepwalk.domain.model.mission.MissionComposite
import jinproject.stepwalk.domain.model.mission.MissionFigure
import jinproject.stepwalk.domain.model.mission.MissionList
import jinproject.stepwalk.domain.model.mission.StepMission
import jinproject.stepwalk.mission.screen.component.MissionBadge
import jinproject.stepwalk.mission.screen.component.MissionMedal
import jinproject.stepwalk.mission.screen.missiondetail.component.MissionCommonView
import jinproject.stepwalk.mission.screen.missiondetail.component.MissionCompositeView
import jinproject.stepwalk.mission.util.getIcon

@Composable
internal fun MissionDetailScreen(
    missionDetailViewModel: MissionDetailViewModel = hiltViewModel(),
    popBackStack: () -> Unit
) {
    val missionList by missionDetailViewModel.missionList.collectAsStateWithLifecycle()

    MissionDetailScreen(
        title = missionDetailViewModel.title,
        detailList = missionList,
        popBackStack = popBackStack
    )
}

@Composable
private fun MissionDetailScreen(
    title: String,
    detailList: MissionList,
    popBackStack: () -> Unit
) {
    var selectMission by remember { mutableStateOf<MissionFigure>(detailList.list.first()) } //초기값으로 현재 진행중인 값 넣도록진행
    LaunchedEffect(key1 = detailList) {
        selectMission =
            detailList.list.find { it.getMissionAchieved() > 0 && it.getMissionAchieved() < it.getMissionGoal() }
                ?: detailList.list.last()
    }

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
                .weight(0.65f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectMission is MissionComposite) {
                MissionCompositeView(selectMission = selectMission as MissionComposite)
            } else {
                MissionCommonView(selectMission = selectMission as MissionCommon)
            }
        }

        Column(
            modifier = Modifier
                .weight(0.35f)
                .shadow(
                    elevation = 12.dp,
                    shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
                )
                .background(MaterialTheme.colorScheme.surface)
                .fillMaxWidth()
        ) {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = rememberLazyGridState(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(if (detailList.list.first() is MissionComposite) 10.dp else 0.dp),
            ) {
                if (detailList.list.first() is MissionComposite) {
                    if (detailList.list.size == 1) {//시간 미션(주간,월간)
                        items(
                            items = (detailList.list.first() as MissionComposite).missions,
                            key = { it.hashCode() }) { mission ->
                            MissionBadge(
                                modifier = Modifier.size(120.dp),
                                icon = mission.getIcon(),
                                mission = mission,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    } else {//통합 미션
                        items(items = detailList.list, key = { it.designation }) { mission ->
                            MissionBadge(
                                modifier = Modifier.size(120.dp),
                                icon = mission.getIcon(),
                                mission = mission,
                                color = MaterialTheme.colorScheme.primary,
                                onClick = { thisMission -> selectMission = thisMission }
                            )
                        }
                    }
                } else {//목표미션
                    items(items = detailList.list, key = { it.designation }) { mission ->
                        MissionMedal(
                            modifier = Modifier.size(120.dp),
                            icon = mission.getIcon(),
                            mission = mission,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { thisMission -> selectMission = thisMission }
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
    MissionDetailScreen(
        title = "칼로리",
        detailList = MissionList(
            title = "일일 미션",
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

