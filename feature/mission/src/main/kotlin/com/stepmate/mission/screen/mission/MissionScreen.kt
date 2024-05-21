package com.stepmate.mission.screen.mission

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import com.stepmate.design.component.DefaultButton
import com.stepmate.design.component.DescriptionSmallText
import com.stepmate.design.component.StepMateProgressIndicatorRotating
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.design.component.layout.DefaultLayout
import com.stepmate.design.component.layout.ExceptionScreen
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.domain.model.mission.MissionCommon
import com.stepmate.mission.screen.mission.MissionViewModel.Companion.CANNOT_LOGIN_EXCEPTION
import com.stepmate.mission.screen.mission.component.MissionItem

@Composable
internal fun MissionScreen(
    missionViewModel: MissionViewModel = hiltViewModel(),
    navigateToMissionDetail: (String) -> Unit,
    navigateToLogin: (NavOptions?) -> Unit
) {
    val uiState by missionViewModel.uiState.collectAsStateWithLifecycle(initialValue = MissionViewModel.UiState.Loading)
    val missionListState by missionViewModel.missionList.collectAsStateWithLifecycle()

    when (uiState) {
        is MissionViewModel.UiState.Error -> {
            val exception = (uiState as MissionViewModel.UiState.Error).exception
            if (exception == CANNOT_LOGIN_EXCEPTION && exception.message == CANNOT_LOGIN_EXCEPTION.message) {
                ExceptionScreen(
                    headlineMessage = "로그인을 하실 수 없어요.",
                    causeMessage = "로그인을 하셔야 미션 기능을 이용하실 수 있어요.",
                    content = {
                        VerticalSpacer(height = 20.dp)
                        DefaultButton(onClick = {
                            navigateToLogin(null)
                        }) {
                            DescriptionSmallText(
                                text = "로그인 하러 가기",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            } else
                ExceptionScreen(
                    headlineMessage = "미션 기능을 이용할 수 없어요.",
                    causeMessage = (uiState as MissionViewModel.UiState.Error).exception.message.toString(),
                )
        }

        MissionViewModel.UiState.Loading -> {
            StepMateProgressIndicatorRotating()
        }

        MissionViewModel.UiState.Success -> {
            MissionScreen(
                missionList = missionListState,
                navigateToMissionDetail = navigateToMissionDetail
            )
        }
    }
}

@Composable
private fun MissionScreen(
    missionList: List<List<MissionCommon>>,
    navigateToMissionDetail: (String) -> Unit
) {
    DefaultLayout(
        modifier = Modifier.statusBarsPadding(),
        contentPaddingValues = PaddingValues(horizontal = 12.dp)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize(),
            state = rememberLazyListState(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            item { }
            items(items = missionList, key = { it.hashCode() }) { missionList ->
                MissionItem(
                    missionList = missionList,
                    onClick = {
                        navigateToMissionDetail(missionList.first().getMissionTitle())
                    }
                )
            }
        }
    }
}


@Composable
@Preview
private fun PreviewMissionScreen(

) = StepMateTheme {
    MissionScreen(
        missionList = listOf(),
        navigateToMissionDetail = { _ -> }
    )
}