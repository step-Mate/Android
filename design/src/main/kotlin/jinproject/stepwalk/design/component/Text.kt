package jinproject.stepwalk.design.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import jinproject.stepwalk.design.theme.StepWalkTheme

@Composable
fun AppBarText(
    modifier: Modifier = Modifier,
    text: String,
) {
    DefaultText(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.headlineSmall,
    )
}

@Composable
fun BottomBarText(
    modifier: Modifier = Modifier,
    text: String,
    clicked: Boolean,
) {
    DefaultText(
        modifier = modifier,
        text = text,
        style = if (clicked) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyLarge,
        color = if (clicked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.inverseOnSurface
    )
}

@Composable
fun HeadlineText(
    modifier: Modifier = Modifier,
    text: String,
    maxLines: Int = 1,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    DefaultText(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        color = color,
        maxLines = maxLines
    )
}

@Composable
fun DescriptionLargeText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    DefaultText(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        overflow = Ellipsis,
        color = color,
        maxLines = Int.MAX_VALUE,
    )
}

@Composable
fun DescriptionSmallText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    DefaultText(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodySmall,
        overflow = Ellipsis,
        color = color,
        maxLines = Int.MAX_VALUE,
    )
}

@Composable
fun FooterText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onBackground,
) {
    DefaultText(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.labelSmall,
        color = color,
        maxLines = Int.MAX_VALUE,
    )
}

@Composable
fun DefaultText(
    modifier: Modifier = Modifier,
    text: String,
    style: TextStyle,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.Start,
    overflow: TextOverflow = TextOverflow.Clip,
    maxLines: Int = 1,
    onTextLayout: (TextLayoutResult) -> Unit = {},
) {
    Text(
        text = text,
        modifier = modifier,
        style = style,
        color = color,
        textAlign = textAlign,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewBottomBarTextTrue() = StepWalkTheme {
    BottomBarText(
        text = "길면 잘려서 보이게 됩니다.길면 잘려서 보이게 됩니다.길면 잘려서 보이게 됩니다.길면 잘려서 보이게 됩니다.길면 잘려서 보이게 됩니다.",
        clicked = true
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewBottomBarTextFalse() = StepWalkTheme {
    BottomBarText(
        text = "길면 잘려서 보이게 됩니다.길면 잘려서 보이게 됩니다.길면 잘려서 보이게 됩니다.길면 잘려서 보이게 됩니다.",
        clicked = false
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewAppBarText() = StepWalkTheme {
    AppBarText(
        text = "이렇게 보입니다.길면 잘려서 보이게 됩니다.",
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewHeadlineText() = StepWalkTheme {
    HeadlineText(
        text = "이렇게 보입니다. 길면 잘리게 됩니다. 이렇게 잘리게 됩니다.",
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewDescriptionText() = StepWalkTheme {
    DescriptionSmallText(
        text = "이렇게 보입니다. 길어도 이렇게 계속 잘 보이게 됩니다. 짤리지 않고 쭈우우욱",
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewFooterText() = StepWalkTheme {
    FooterText(
        text = "이렇게 보입니다. 길어도 이렇게 계속 잘 보이게 됩니다. 짤리지 않고 쭈우우욱",
    )
}
