package jinproject.stepwalk.mission.screen.mission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.StepMateProgressIndicatorRotating
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.domain.model.mission.MissionList
import jinproject.stepwalk.mission.screen.mission.component.MissionItem

@Composable
internal fun MissionScreen(
    missionViewModel: MissionViewModel = hiltViewModel(),
    navigateToMissionDetail : (String) -> Unit,
) {
    val uiState by missionViewModel.uiState.collectAsStateWithLifecycle(initialValue = MissionViewModel.UiState.Loading)
    val missionList by missionViewModel.missionList.collectAsStateWithLifecycle()

    MissionScreen(
        uiState = uiState,
        missionList = missionList,
        navigateToMissionDetail = navigateToMissionDetail
    )
}

@Composable
private fun MissionScreen(
    uiState : MissionViewModel.UiState,
    missionList : List<MissionList>,
    navigateToMissionDetail : (String) -> Unit
){
    when(uiState){
        is MissionViewModel.UiState.Error ->{

        }
        MissionViewModel.UiState.Loading -> {
            StepMateProgressIndicatorRotating()
        }
        MissionViewModel.UiState.Success -> {
            DefaultLayout(
                modifier = Modifier.windowInsetsPadding(WindowInsets.statusBars),
                contentPaddingValues = PaddingValues(horizontal = 12.dp)
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize(),
                    state = rememberLazyListState(),
                    verticalArrangement = Arrangement.spacedBy(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ){
                    item {
                    }
                    items(items = missionList, key = {it.title}){missionList ->
                        MissionItem(
                            missionList = missionList,
                            onClick = {
                                navigateToMissionDetail(missionList.title)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewMissionScreen(

) = StepWalkTheme {
    MissionScreen(
        uiState = MissionViewModel.UiState.Success,
        missionList = listOf(),
        navigateToMissionDetail = {_ ->}
    )
}