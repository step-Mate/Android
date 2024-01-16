package jinproject.stepwalk.mission.screen.missondetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.mission.component.MissionBar
import jinproject.stepwalk.mission.screen.state.Mission
import jinproject.stepwalk.mission.screen.state.MissionList

@Composable
internal fun MissionDetailScreen(
    missionDetailViewModel: MissionDetailViewModel = hiltViewModel()
) {

    MissionDetailScreen(
        mission = MissionList.list[0]
    )
}

@Composable
private fun MissionDetailScreen(
    mission : Mission
){
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("ic_anim_box2.json"))
    val lottieProgress by animateLottieCompositionAsState(composition, iterations = LottieConstants.IterateForever)
    DefaultLayout(
        contentPaddingValues = PaddingValues(vertical = 20.dp, horizontal = 12.dp)
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            VerticalSpacer(height = 30.dp)
            LottieAnimation(
                composition = composition, 
                progress = { lottieProgress },
                modifier = Modifier.size(200.dp)
            )
            VerticalSpacer(height = 20.dp)
            MissionBar(
                modifier = Modifier.padding(bottom = 12.dp),
                nowValue = mission.missionValue.nowValue,
                maxValue = mission.missionValue.maxValue,
                textColor = mission.containerColor,
                progressColor = mission.containerColor
            )
            VerticalSpacer(height = 30.dp)

            LazyColumn(
                modifier = Modifier.weight(0.4f),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){
                itemsIndexed(MissionList.list){index, item ->

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

    )
}