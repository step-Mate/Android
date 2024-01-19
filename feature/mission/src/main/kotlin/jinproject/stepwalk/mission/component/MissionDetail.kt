package jinproject.stepwalk.mission.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import jinproject.stepwalk.design.component.HeadlineText
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.mission.screen.state.MissionValue

@Composable
internal fun MissionTimeTop(
    missionValue : MissionValue,
    isReward : Boolean,
    onClick : () -> Unit
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("ic_anim_box.json"))
    val lottieProgress by animateLottieCompositionAsState(composition, isPlaying = isReward, iterations = LottieConstants.IterateForever)

    LottieAnimation(
        composition = composition,
        progress = { lottieProgress },
        modifier = Modifier
            .size(200.dp)
            .padding(top = 20.dp)
            .clickable(
                enabled = isReward,
                onClick = onClick
            )
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
    isReward : Boolean,
    onClick: () -> Unit
){
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("ic_anim_box.json"))
    val lottieProgress by animateLottieCompositionAsState(composition, isPlaying = isReward, iterations = LottieConstants.IterateForever)

    if (missionValue.isMatched() && isReward){//아직 보상 수령 안했을 경우
        LottieAnimation(
            composition = composition,
            progress = { lottieProgress },
            modifier = Modifier
                .size(180.dp)
                .padding(top = 30.dp)
                .clickable(
                    onClick = onClick
                )
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
            .shadow(
                elevation = 8.dp,
                shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
            )
            .background(MaterialTheme.colorScheme.secondary)
            .fillMaxWidth()
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