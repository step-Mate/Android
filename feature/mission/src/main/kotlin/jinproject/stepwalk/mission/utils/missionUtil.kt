package jinproject.stepwalk.mission.utils

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.unit.dp

@Composable
internal fun Modifier.detailBottomSheet() =
    this.shadow(
        elevation = 8.dp,
        shape = RoundedCornerShape(topStart = 30.dp, topEnd = 30.dp)
    )
        .background(MaterialTheme.colorScheme.secondary)
        .fillMaxWidth()