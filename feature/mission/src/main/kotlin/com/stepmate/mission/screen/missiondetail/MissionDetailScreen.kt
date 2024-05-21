package com.stepmate.mission.screen.missiondetail

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomSheetScaffold
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberBottomSheetScaffoldState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stepmate.design.R
import com.stepmate.design.component.HeadlineText
import com.stepmate.design.component.StepMateBoxDefaultTopBar
import com.stepmate.design.component.StepMateProgressIndicatorRotating
import com.stepmate.design.component.layout.ExceptionScreen
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.domain.model.mission.MissionCommon
import com.stepmate.domain.model.mission.MissionComposite
import com.stepmate.domain.model.mission.MissionFigure
import com.stepmate.domain.model.mission.StepMission
import com.stepmate.mission.screen.component.MissionBadge
import com.stepmate.mission.screen.component.MissionMedal
import com.stepmate.mission.screen.missiondetail.component.MissionCommonView
import com.stepmate.mission.screen.missiondetail.component.MissionCompositeView
import com.stepmate.mission.util.getIcon
import kotlinx.coroutines.launch

@Composable
internal fun MissionDetailScreen(
    missionDetailViewModel: MissionDetailViewModel = hiltViewModel(),
    popBackStack: () -> Unit
) {
    val uiState by missionDetailViewModel.uiState.collectAsStateWithLifecycle(initialValue = MissionDetailViewModel.UiState.Loading)
    val missionList by missionDetailViewModel.missionList.collectAsStateWithLifecycle()

    when (uiState) {
        is MissionDetailViewModel.UiState.Error -> {
            ExceptionScreen(
                headlineMessage = "미션 기능을 이용할 수 없어요.",
                causeMessage = (uiState as MissionDetailViewModel.UiState.Error).exception.message.toString(),
            )
        }

        MissionDetailViewModel.UiState.Loading -> {
            StepMateProgressIndicatorRotating()
        }

        MissionDetailViewModel.UiState.Success -> {
            MissionDetailScreen(
                title = missionDetailViewModel.title,
                missionList = missionList,
                popBackStack = popBackStack
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun MissionDetailScreen(
    title: String,
    missionList: List<MissionCommon>,
    popBackStack: () -> Unit
) {
    var selectMission by remember { mutableStateOf<MissionFigure>(missionList.first()) }
    val missionState by remember { mutableStateOf(missionList.first() is MissionComposite) }
    val scaffoldState = rememberBottomSheetScaffoldState()
    var designation by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    LaunchedEffect(key1 = missionList) {
        selectMission =
            if (missionList.first().getMissionAchieved() == 0) missionList.first() else
                missionList.find { it.getMissionAchieved() > 0 && it.getMissionAchieved() < it.getMissionGoal() }
                    ?: missionList.last()
        designation =
            missionList.find { it.getMissionAchieved() < it.getMissionGoal() }?.designation
                ?: ""
    }
    BottomSheetScaffold(
        modifier = Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        sheetContent = {
            LazyVerticalGrid(
                columns = GridCells.Fixed(3),
                state = rememberLazyGridState(),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 10.dp, start = 10.dp, end = 10.dp),
                verticalArrangement = Arrangement.spacedBy(if (missionState) 10.dp else 0.dp),
            ) {
                if (missionState) { //통합 미션
                    items(items = missionList, key = { it.designation }) { mission ->
                        MissionBadge(
                            modifier = Modifier.size(120.dp),
                            icon = mission.getIcon(),
                            mission = mission,
                            animate = mission.designation == designation,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { thisMission ->
                                selectMission = thisMission
                                coroutineScope.launch {
                                    scaffoldState.bottomSheetState.partialExpand()
                                }
                            }
                        )
                    }
                } else {//목표미션
                    items(items = missionList, key = { it.designation }) { mission ->
                        MissionMedal(
                            modifier = Modifier
                                .height(110.dp)
                                .width(90.dp),
                            icon = mission.getIcon(),
                            mission = mission,
                            animate = mission.designation == designation,
                            color = MaterialTheme.colorScheme.primary,
                            onClick = { thisMission ->
                                selectMission = thisMission
                                coroutineScope.launch {
                                    scaffoldState.bottomSheetState.partialExpand()
                                }
                            }
                        )
                    }
                }
            }
        },
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
        },
        sheetPeekHeight = 250.dp,
        sheetShape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp),
        sheetShadowElevation = 12.dp,
        sheetContainerColor = MaterialTheme.colorScheme.surface,
        containerColor = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (selectMission is MissionComposite) {
                MissionCompositeView(
                    selectMission = selectMission as MissionComposite,
                    designation = designation
                )
            } else {
                MissionCommonView(
                    selectMission = selectMission as MissionCommon,
                    designation = designation
                )
            }
        }
    }
}


@Composable
@Preview
private fun PreviewMissionRepeatScreen(

) = StepMateTheme {
    MissionDetailScreen(
        title = "칼로리",
        missionList =
        listOf(
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
        ),
        popBackStack = {}
    )
}

