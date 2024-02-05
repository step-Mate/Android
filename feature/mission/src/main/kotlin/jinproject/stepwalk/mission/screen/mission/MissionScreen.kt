package jinproject.stepwalk.mission.screen.mission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.domain.model.MissionList
import jinproject.stepwalk.domain.model.MissionMode
import jinproject.stepwalk.mission.screen.mission.component.MissionItem


@Composable
internal fun MissionScreen(
    missionViewModel: MissionViewModel = hiltViewModel(),
    navigateToMissionDetail : (String,MissionMode) -> Unit,
) {
    MissionScreen(
        missionList = missionViewModel.missionList.toList(),
        navigateToMissionDetail = navigateToMissionDetail
    )
}

@Composable
private fun MissionScreen(
    missionList : List<MissionList>,
    navigateToMissionDetail : (String,MissionMode) -> Unit
){
    DefaultLayout(
        contentPaddingValues = PaddingValues(vertical = 30.dp, horizontal = 12.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = rememberLazyListState(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ){
            items(items = missionList, key = {it.title}){missionList ->
                MissionItem(
                    missionList = missionList,
                    onClick = {
                        navigateToMissionDetail(missionList.title,it)
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