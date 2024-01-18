package jinproject.stepwalk.mission.component


import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.LottieComposition
import com.airbnb.lottie.compose.LottieAnimation
import jinproject.stepwalk.design.component.HeadlineText
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.mission.screen.state.MissionValue
import jinproject.stepwalk.mission.utils.detailBottomSheet

@Composable
internal fun MissionTimeTop(
    missionValue : MissionValue,
    composition : LottieComposition?,
    lottieProgress : Float
) {


    LottieAnimation(
        composition = composition,
        progress = { lottieProgress },
        modifier = Modifier
            .size(200.dp)
            .padding(top = 20.dp)
    )
    MissionBar(
        modifier = Modifier
            .padding(top = 10.dp),
        missionValue = missionValue,
        height = 20.dp,
        textColor = StepWalkColor.blue_400.color,
        progressColor = StepWalkColor.blue_400.color
    )
}

@Composable
internal fun MissionRepeatTop(
    missionValue: MissionValue,
    title: String,
    composition : LottieComposition?,
    lottieProgress : Float
){
    if (missionValue.isMatched()){//아직 보상 수령 안했을 경우
        LottieAnimation(
            composition = composition,
            progress = { lottieProgress },
            modifier = Modifier
                .size(180.dp)
                .padding(top = 30.dp)
        )
    }else{
        AnimatedCircularProgressIndicator(
            text = title,
            missionValue = missionValue,
            modifier = Modifier
                .size(180.dp)
                .padding(top = 30.dp)
        )
    }

}

@Composable
internal fun MissionDetailBottom(
    modifier: Modifier = Modifier,
    text : String,
    lazyColumn : @Composable ()-> Unit
){
    Column(
        modifier = modifier
            .detailBottomSheet()
    ) {
        HeadlineText(
            modifier = Modifier
                .padding(top = 20.dp, bottom = 15.dp)
                .align(Alignment.CenterHorizontally),
            text = text,
            textAlign = TextAlign.Center
        )
        lazyColumn()
    }
}