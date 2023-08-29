package jinproject.stepwalk.home.component

import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.PreviewStepWalkTheme

@Composable
internal fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit,
    onClickIcon1: () -> Unit,
    onClickIcon2: () -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .shadow(4.dp, RectangleShape, clip = false)
            .background(MaterialTheme.colorScheme.surface)
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.CenterStart),
            onClick = onBackClick,
            interactionSource = remember { MutableInteractionSource() }
        ) {
            Icon(
                painter = painterResource(id = jinproject.stepwalk.design.R.drawable.ic_arrow_down_small),
                contentDescription = "back",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        Row(
            modifier = Modifier
                .align(Alignment.CenterEnd)
        ) {
            IconButton(onClick = onClickIcon1) {
                Icon(
                    painter = painterResource(id = jinproject.stepwalk.design.R.drawable.ic_setting),
                    contentDescription = "GearIcon",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(onClick = onClickIcon2) {
                Icon(
                    painter = painterResource(id = jinproject.stepwalk.design.R.drawable.ic_home),
                    contentDescription = "AlarmIcon",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeTopAppBar() =
    PreviewStepWalkTheme {
        HomeTopAppBar(
            modifier = Modifier,
            onBackClick = {},
            onClickIcon1 = {},
            onClickIcon2 = {}
        )
    }