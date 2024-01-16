package jinproject.stepwalk.mission.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
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
import jinproject.stepwalk.mission.component.MissionItem
import jinproject.stepwalk.mission.screen.state.MissionList

@Composable
internal fun MissionScreen(
    missionViewModel: MissionViewModel = hiltViewModel()
) {

    MissionScreen()
}

@Composable
private fun MissionScreen(

){
    DefaultLayout(
        contentPaddingValues = PaddingValues(vertical = 20.dp, horizontal = 12.dp)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ){
            items(MissionList.list){
                MissionItem(
                    mission = it,
                    onClick = {}

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

    )
}