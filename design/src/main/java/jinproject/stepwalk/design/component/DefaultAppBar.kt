package jinproject.stepwalk.design.component

import androidx.annotation.DrawableRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.PreviewStepWalkTheme
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.design.theme.Typography

@Composable
fun TitleAppBar(
    title: String,
    @DrawableRes startIcon: Int,
    onBackClick: () -> Unit
) {
    DefaultAppBar(
        onBackClick = onBackClick,
        icon = startIcon
    ) {
        Text(
            modifier = Modifier
                .align(Alignment.Center),
            text = title,
            style = Typography.headlineSmall,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun DefaultAppBar(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    onBackClick: () -> Unit,
    content: @Composable BoxScope.() -> Unit,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .shadow(4.dp, RectangleShape, clip = false)
            .background(MaterialTheme.colorScheme.surface)
            .padding(top = 30.dp, bottom = 10.dp)
    ) {
        IconButton(
            onClick = onBackClick,
            modifier = Modifier
                .align(Alignment.CenterStart)
        ) {
            Icon(
                painter = painterResource(id = icon),
                contentDescription = "backButton",
                tint = MaterialTheme.colorScheme.onSurface
            )
        }
        content()
    }
}

@Preview
@Composable
private fun PreviewDefaultAppBar() =
     StepWalkTheme{
        DefaultAppBar(
            onBackClick = {},
            content = {},
            icon = R.drawable.ic_arrow_left_small
        )
    }

@Preview
@Composable
private fun PreviewTitleAppBar() =
    StepWalkTheme{
        TitleAppBar(
            title = "타이틀입니다.",
            onBackClick = {},
            startIcon = R.drawable.ic_arrow_left_small
        )
    }