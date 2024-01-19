package jinproject.stepwalk.mission.screen.mission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import jinproject.stepwalk.mission.component.MissionItem
import jinproject.stepwalk.mission.screen.state.Mission
import jinproject.stepwalk.mission.screen.state.MissionMode

@Composable
internal fun MissionScreen(
    missionViewModel: MissionViewModel = hiltViewModel(),
    navigateToMissionDetail : (String,MissionMode) -> Unit,
) {
    MissionScreen(
        missionList = missionViewModel.missionList,
        navigateToMissionDetail = navigateToMissionDetail
    )
}

@Composable
private fun MissionScreen(
    missionList : List<Mission>,
    navigateToMissionDetail : (String,MissionMode) -> Unit
){
    DefaultLayout(
        contentPaddingValues = PaddingValues(vertical = 30.dp, horizontal = 12.dp)
    ) {
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
        ){
            items(missionList, key = {it.title.title}){
                MissionItem(
                    mission = it,
                    onClick = {
                        navigateToMissionDetail(it.title.title,it.title.mode)
                    }
                )
            }
        }
    }
}

@Composable
@Preview
private fun PreviewMissionScreen(

) = StepWalkTheme {
    MissionScreen(
        missionList = listOf(),
        navigateToMissionDetail = {_,_ ->}
    )
}