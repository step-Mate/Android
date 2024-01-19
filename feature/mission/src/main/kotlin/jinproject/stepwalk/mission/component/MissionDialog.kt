package jinproject.stepwalk.mission.component

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.HeadlineText
import jinproject.stepwalk.design.theme.StepWalkTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MissionDialog(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    reward : Int,
    properties: DialogProperties = DialogProperties(dismissOnBackPress = true,dismissOnClickOutside = true)
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset("ic_anim_reward.json"))
    val lottieProgress by animateLottieCompositionAsState(composition, isPlaying = true, iterations = 1)

    AlertDialog(
        onDismissRequest = onDismissRequest,
        modifier = modifier
            .clickable(onClick = onDismissRequest)
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.background),
        properties = properties
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            HeadlineText(
                text = "보상 수령",
                modifier = Modifier.padding(top = 20.dp)
            )
            LottieAnimation(
                composition = composition,
                progress = { lottieProgress },
                modifier = Modifier
                    .size(180.dp)
                    .padding(top = 20.dp)
            )
            DescriptionLargeText(
                text = "+ ${reward}XP",
                modifier = Modifier.padding(top = 10.dp, bottom = 20.dp)
            )
        }
    }
}

@Composable
@Preview(backgroundColor = 0xFFFFFFFF)
private fun PreviewDialog(
    
) = StepWalkTheme {
    MissionDialog(
        onDismissRequest = { },
        reward = 10
    )
}