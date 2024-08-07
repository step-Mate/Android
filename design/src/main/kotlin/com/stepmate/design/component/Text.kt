package com.stepmate.design.component

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.style.TextOverflow.Companion.Ellipsis
import androidx.compose.ui.tooling.preview.Preview
import com.stepmate.design.theme.StepMateTheme

@Composable
fun AppBarText(
    modifier: Modifier = Modifier,
    text: String,
    textAlign: TextAlign = TextAlign.Start,
) {
    DefaultText(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = textAlign,
    )
}

@Composable
fun BottomBarText(
    modifier: Modifier = Modifier,
    text: String,
    clicked: Boolean,
    textAlign: TextAlign = TextAlign.Start,
) {
    DefaultText(
        modifier = modifier,
        text = text,
        style = if (clicked) MaterialTheme.typography.labelLarge else MaterialTheme.typography.bodyLarge,
        color = if (clicked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.inverseOnSurface,
        textAlign = textAlign,
    )
}

@Composable
fun HeadlineText(
    modifier: Modifier = Modifier,
    text: String,
    maxLines: Int = 1,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.Start,
) {
    DefaultText(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.headlineSmall,
        textAlign = textAlign,
        color = color,
        maxLines = maxLines
    )
}

@Composable
fun DescriptionLargeText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.Start,
) {
    DefaultText(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = textAlign,
        overflow = Ellipsis,
        color = color,
        maxLines = Int.MAX_VALUE,
    )
}

@Composable
fun DescriptionSmallText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.Start,
) {
    DefaultText(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodySmall,
        textAlign = textAlign,
        overflow = Ellipsis,
        color = color,
        maxLines = Int.MAX_VALUE,
    )
}

@Composable
fun FooterText(
    modifier: Modifier = Modifier,
    text: String,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textDecoration: TextDecoration = TextDecoration.None,
    textAlign: TextAlign = TextAlign.Start,
) {
    DefaultText(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.labelSmall,
        textAlign = textAlign,
        textDecoration = textDecoration,
        overflow = Ellipsis,
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
    textDecoration: TextDecoration = TextDecoration.None,
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
        textDecoration = textDecoration,
        overflow = overflow,
        maxLines = maxLines,
        onTextLayout = onTextLayout,
    )
}

@Composable
fun DefaultAnnotatedText(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
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
fun DescriptionAnnotatedLargeText(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.Start,
) {
    DefaultAnnotatedText(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = textAlign,
        overflow = Ellipsis,
        color = color,
        maxLines = Int.MAX_VALUE,
    )
}

@Composable
fun DescriptionAnnotatedSmallText(
    modifier: Modifier = Modifier,
    text: AnnotatedString,
    color: Color = MaterialTheme.colorScheme.onSurface,
    textAlign: TextAlign = TextAlign.Start,
) {
    DefaultAnnotatedText(
        modifier = modifier,
        text = text,
        style = MaterialTheme.typography.bodySmall,
        textAlign = textAlign,
        overflow = Ellipsis,
        color = color,
        maxLines = Int.MAX_VALUE,
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewBottomBarTextTrue() = StepMateTheme {
    BottomBarText(
        text = "길면 잘려서 보이게 됩니다.길면 잘려서 보이게 됩니다.길면 잘려서 보이게 됩니다.길면 잘려서 보이게 됩니다.길면 잘려서 보이게 됩니다.",
        clicked = true
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewBottomBarTextFalse() = StepMateTheme {
    BottomBarText(
        text = "길면 잘려서 보이게 됩니다.길면 잘려서 보이게 됩니다.길면 잘려서 보이게 됩니다.길면 잘려서 보이게 됩니다.",
        clicked = false
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewAppBarText() = StepMateTheme {
    AppBarText(
        text = "이렇게 보입니다.길면 잘려서 보이게 됩니다.",
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewHeadlineText() = StepMateTheme {
    HeadlineText(
        text = "이렇게 보입니다. 길면 잘리게 됩니다. 이렇게 잘리게 됩니다.",
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewDescriptionText() = StepMateTheme {
    DescriptionSmallText(
        text = "이렇게 보입니다. 길어도 이렇게 계속 잘 보이게 됩니다. 짤리지 않고 쭈우우욱",
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = 0xFFFFFF)
private fun PreviewFooterText() = StepMateTheme {
    FooterText(
        text = "이렇게 보입니다. 길어도 이렇게 계속 잘 보이게 됩니다. 짤리지 않고 쭈우우욱",
    )
}
