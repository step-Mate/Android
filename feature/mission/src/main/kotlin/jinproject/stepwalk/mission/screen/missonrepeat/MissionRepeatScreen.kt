package jinproject.stepwalk.mission.screen.missonrepeat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.mission.component.MissionLayout
import jinproject.stepwalk.mission.component.MissionRepeatTop
import jinproject.stepwalk.mission.component.MissionSuccessCircleView
import jinproject.stepwalk.mission.screen.state.MissionDetail
import jinproject.stepwalk.mission.screen.state.MissionValue

@Composable
internal fun MissionRepeatScreen(
    missionRepeatViewModel: MissionRepeatViewModel = hiltViewModel()
) {
    MissionRepeatScreen(
        title = missionRepeatViewModel.title,
        missionValue = MissionValue(9,10),
        detailList = missionRepeatViewModel.list,
    )
}

@Composable
private fun MissionRepeatScreen(
    title : String,
    missionValue: MissionValue,
    detailList : List<MissionDetail>,
){
    DefaultLayout(
        contentPaddingValues = PaddingValues(top = 30.dp)
    ) {
        MissionLayout(
            title = "달성 과제",
            reward = 100,
            weight = 0.35f,
            topView = {
                MissionRepeatTop(
                    missionValue = missionValue,
                    title = title,
                    isReward = true,
                    onClick = it
                )
            },
            bottomView = {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ){
                    items(detailList){
                        MissionSuccessCircleView(
                            modifier = Modifier
                                .size(110.dp),
                            text = it.title
                        )
                    }
                }
            }
        )
    }
}

@Composable
@Preview
private fun PreviewMissionDetail(

) = StepWalkTheme {
    MissionRepeatScreen(
        title = "칼로리",
        missionValue = MissionValue(3,10),
        detailList = listOf(MissionDetail("1000000"),MissionDetail("1000000")),
    )
}

