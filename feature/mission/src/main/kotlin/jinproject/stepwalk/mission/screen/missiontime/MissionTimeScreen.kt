package jinproject.stepwalk.mission.screen.missiontime

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.mission.component.MissionBarItem
import jinproject.stepwalk.mission.component.MissionLayout
import jinproject.stepwalk.mission.component.MissionTimeTop
import jinproject.stepwalk.mission.screen.state.MissionDetail
import jinproject.stepwalk.mission.screen.state.MissionValue

@Composable
internal fun MissionTimeScreen(
    missionTimeViewModel: MissionTimeViewModel = hiltViewModel()
) {
    MissionTimeScreen(
        missionValue = MissionValue(4, 10),
        detailList = missionTimeViewModel.list,
    )
}

@Composable
private fun MissionTimeScreen(
    missionValue: MissionValue,
    detailList : List<MissionDetail>,
){
    DefaultLayout(
        contentPaddingValues = PaddingValues(top = 30.dp)
    ) {
        MissionLayout(
            title = "달성 현황",
            reward = 100,
            weight = 0.4f,
            topView = {
                MissionTimeTop(
                    missionValue = missionValue,
                    isReward = true,
                    onClick = it
              )
            },
            bottomView = {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    items(detailList){
                        MissionBarItem(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp),
                            missionDetail = it
                        )
                    }
                }
            }
        )


    }
}

@Composable
@Preview
private fun PreviewMissionTimeScreen(

) = StepWalkTheme {
    MissionTimeScreen(
        missionValue = MissionValue(3,5),
        detailList = listOf(MissionDetail("일주일간 30000걸음 달성하기", MissionValue(300,30000)),
            MissionDetail("일주일간 150kcal 달성하기",MissionValue(120,150))),
    )
}