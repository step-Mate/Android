package jinproject.stepwalk.home.screen.component

import androidx.compose.foundation.layout.Row
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import jinproject.stepwalk.design.PreviewStepWalkTheme
import jinproject.stepwalk.design.component.DefaultAppBar

@Composable
internal fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    onClickTimeIcon: () -> Unit,
    onClickIcon1: () -> Unit,
    onClickIcon2: () -> Unit
) {
    DefaultAppBar(
        modifier = modifier,
        icon = jinproject.stepwalk.design.R.drawable.ic_arrow_down_small,
        onBackClick = onClickTimeIcon
    ) {
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
            onClickTimeIcon = {},
            onClickIcon1 = {},
            onClickIcon2 = {}
        )
    }