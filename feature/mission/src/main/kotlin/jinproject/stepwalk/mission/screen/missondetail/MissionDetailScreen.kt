package jinproject.stepwalk.mission.screen.missondetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.mission.component.MissionBarItem
import jinproject.stepwalk.mission.component.MissionDetailBottom
import jinproject.stepwalk.mission.component.MissionRepeatTop
import jinproject.stepwalk.mission.component.MissionSuccessCircleView
import jinproject.stepwalk.mission.component.MissionTimeTop
import jinproject.stepwalk.mission.screen.state.MissionDetail
import jinproject.stepwalk.mission.screen.state.MissionValue

@Composable
internal fun MissionDetailScreen(
    missionDetailViewModel: MissionDetailViewModel = hiltViewModel()
) {
    MissionDetailScreen(
        title = missionDetailViewModel.title,
        missionValue = MissionValue(4,10),
        detailList = missionDetailViewModel.list,
        mode = missionDetailViewModel.mode
    )
}

@Composable
private fun MissionDetailScreen(
    title : String,
    missionValue: MissionValue,
    detailList : List<MissionDetail>,
    mode : MissionMode
){
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("ic_anim_box2.json"))
    val lottieProgress by animateLottieCompositionAsState(composition, isPlaying = false, iterations = LottieConstants.IterateForever)

    DefaultLayout(
        contentPaddingValues = PaddingValues(top = 30.dp)
    ) {
        if (mode == MissionMode.time){
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.4f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MissionTimeTop(
                    missionValue = missionValue,
                    composition = composition,
                    lottieProgress = lottieProgress
                )
            }
            MissionDetailBottom(
                modifier = Modifier.weight(0.6f) ,
                text = "달성 현황"
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
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


        }else{
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.35f),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                MissionRepeatTop(
                    missionValue = missionValue,
                    title = title,
                    composition = composition,
                    lottieProgress = lottieProgress
                )
            }
            MissionDetailBottom(
                modifier = Modifier.weight(0.65f),
                text = "달성 과제"
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(15.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp),
                ){
                    items(detailList){
                        MissionSuccessCircleView(
                            modifier = Modifier
                                .size(110.dp)
                                .padding(5.dp),
                            text = it.title
                        )
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewMissionDetail(

) = StepWalkTheme {
    MissionDetailScreen(
        title = "칼로리",
        missionValue = MissionValue(3,10),
        detailList = listOf(MissionDetail("1000000"),MissionDetail("1000000")),
        mode = MissionMode.repeat
    )
}

@Composable
@Preview
private fun PreviewMissionDetail2(

) = StepWalkTheme {
    MissionDetailScreen(
        title = "월간 미션",
        missionValue = MissionValue(3,5),
        detailList = listOf(MissionDetail("일주일간 30000걸음 달성하기", MissionValue(300,30000)),
            MissionDetail("일주일간 150kcal 달성하기",MissionValue(120,150))),
        mode = MissionMode.time
    )
}