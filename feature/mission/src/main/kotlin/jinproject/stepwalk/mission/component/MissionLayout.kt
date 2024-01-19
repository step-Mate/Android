package jinproject.stepwalk.mission.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
internal fun MissionLayout(
    title : String,
    reward : Int,
    weight : Float,
    topView : @Composable (onOpen : () -> Unit) -> Unit,
    bottomView : @Composable () -> Unit,
){
    var dialogState by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(weight),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            topView { dialogState = true }
        }
        MissionDetailBottom(
            modifier = Modifier.weight(1-weight),
            text = title,
            lazyColumn = bottomView
        )
    }
    if (dialogState) {
        MissionDialog(
            onDismissRequest = { dialogState = false },
            reward = reward
        )
    }
}