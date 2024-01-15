package jinproject.stepwalk.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.theme.StepWalkTheme

@Composable
fun StepMateTopBar(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    StepMateDefaultTopBar(
        modifier = modifier
            .shadow(4.dp, RectangleShape, clip = false)
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars),
        icon = icon,
        onClick = onClick,
        content = content
    )
}

@Composable
fun BoxScopeTopBar(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    headerContent: @Composable RowScope.() -> Unit,
    bodyContent: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = modifier
            .shadow(4.dp, RectangleShape, clip = false)
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        StepMateDefaultTopBar(
            icon = icon,
            onClick = onClick,
            content = headerContent
        )
        bodyContent()
    }
}

@Composable
fun StepMateDefaultTopBar(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    content: @Composable RowScope.() -> Unit,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier.size(48.dp)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "StepMateTopBarIcon",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        content()
    }
}

@Composable
@Preview
private fun PreviewStepMateDefaultTopBar() = StepWalkTheme {
    StepMateDefaultTopBar(
        icon = R.drawable.ic_arrow_left_small,
        onClick = {},
    ) {

    }
}