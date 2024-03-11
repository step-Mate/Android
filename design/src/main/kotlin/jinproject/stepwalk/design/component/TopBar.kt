package jinproject.stepwalk.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
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
import androidx.compose.ui.Alignment
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
    content: @Composable RowScope.() -> Unit = {},
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
fun StepMateTitleTopBar(
    modifier: Modifier = Modifier,
    text: String,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit = {},
) {
    StepMateBoxDefaultTopBar(
        modifier = modifier
            .shadow(4.dp, RectangleShape, clip = false)
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars),
        icon = icon,
        onClick = onClick,
    ) {
        AppBarText(
            text = text,
            modifier = Modifier.align(Alignment.Center)
        )
        content()
    }
}

@Composable
fun ColumnScopeTopBar(
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
    content: @Composable RowScope.() -> Unit = {},
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        DefaultIconButton(
            icon = icon,
            onClick = onClick,
            iconTint = MaterialTheme.colorScheme.onSurface
        )
        content()
    }
}

@Composable
fun StepMateBoxDefaultTopBar(
    modifier: Modifier = Modifier,
    iconAlignment: Alignment = Alignment.CenterStart,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit = {},
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
    ) {
        DefaultIconButton(
            modifier = Modifier
                .align(iconAlignment),
            icon = icon,
            onClick = onClick,
            iconTint = MaterialTheme.colorScheme.onSurface
        )
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

@Composable
@Preview
private fun PreviewStepMateBoxDefaultTopBar() = StepWalkTheme {
    StepMateBoxDefaultTopBar(
        icon = R.drawable.ic_arrow_left_small,
        onClick = {},
    ) {

    }
}