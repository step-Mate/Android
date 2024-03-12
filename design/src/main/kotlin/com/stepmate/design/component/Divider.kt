package com.stepmate.design.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.stepmate.design.theme.StepMateTheme

@Composable
fun VerticalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    Spacer(
        modifier = modifier
            .fillMaxHeight()
            .width(thickness)
            .background(color)
    )
}

@Suppress("FunctionName")
fun LazyListScope.VerticalDividerItem(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color,
) {
    item {
        VerticalDivider(
            modifier = modifier,
            thickness = thickness,
            color = color
        )
    }
}

@Composable
fun HorizontalDivider(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color = MaterialTheme.colorScheme.outlineVariant,
) {
    Spacer(
        modifier = modifier
            .fillMaxWidth()
            .height(thickness)
            .background(color)
    )
}

@Suppress("FunctionName")
fun LazyListScope.HorizontalDividerItem(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color,
) {
    item {
        HorizontalDivider(
            modifier = modifier,
            thickness = thickness,
            color = color
        )
    }
}

@Composable
fun LazyItemScope.HorizontalDividerItem(
    modifier: Modifier = Modifier,
    thickness: Dp = 1.dp,
    color: Color,
    isLastItem: Boolean = false,
) {
    if (!isLastItem)
        HorizontalDivider(
            modifier = modifier,
            thickness = thickness,
            color = color
        )
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun VerticalDividerPreview() = StepMateTheme {
    Box(contentAlignment = Alignment.Center) {
        VerticalDivider()
    }
}

@Preview(showBackground = true, widthDp = 320, heightDp = 320)
@Composable
private fun HorizontalDividerPreview() = StepMateTheme {
    Box(contentAlignment = Alignment.Center) {
        HorizontalDivider()
    }
}