package jinproject.stepwalk.home.screen.home.component

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.PreviewStepWalkTheme
import jinproject.stepwalk.design.component.BoxScopeTopBar
import jinproject.stepwalk.design.component.HorizontalWeightSpacer

@Composable
internal fun HomeTopAppBar(
    modifier: Modifier = Modifier,
    onClickTime: () -> Unit,
    onClickSetting: () -> Unit,
    onClickHome: () -> Unit,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    BoxScopeTopBar(
        modifier = modifier
            .height(200.dp)
            .fillMaxWidth(),
        icon = jinproject.stepwalk.design.R.drawable.ic_arrow_down_small,
        onClick = onClickTime,
        headerContent = {
            HorizontalWeightSpacer(float = 1f)
            IconButton(
                onClick = onClickSetting,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = jinproject.stepwalk.design.R.drawable.ic_setting),
                    contentDescription = "SettingIcon",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
            IconButton(
                onClick = onClickHome,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    painter = painterResource(id = jinproject.stepwalk.design.R.drawable.ic_home),
                    contentDescription = "HomeIcon",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        bodyContent = content
    )
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeTopAppBar() =
    PreviewStepWalkTheme {
        HomeTopAppBar(
            modifier = Modifier,
            onClickTime = {},
            onClickSetting = {},
            onClickHome = {}
        )
    }