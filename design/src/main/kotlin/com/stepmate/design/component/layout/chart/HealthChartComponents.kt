package com.stepmate.design.component.layout.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import com.stepmate.design.component.DescriptionSmallText
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.design.theme.StepWalkColor

@Composable
fun StepBar(
    index: Int,
    graph: List<Long>,
    modifier: Modifier = Modifier,
    selectChartItem: (PopUpState) -> Unit,
    barColor: List<Color>,
) {
    Spacer(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                selectChartItem(
                    PopUpState(
                        enabled = true,
                        index = index,
                    )
                )
            }
            .padding(horizontal = 2.dp)
            .drawWithCache {
                val brush = Brush.verticalGradient(
                    colors = barColor
                )

                val item = graph[index]
                val maxItem = graph.maxOrNull() ?: 0

                val itemHeight = -(item.stepToSizeByMax(
                    barHeight = size.height,
                    max = maxItem
                ))

                val height = if (itemHeight > 0) 0f else -(item.stepToSizeByMax(
                    barHeight = size.height,
                    max = maxItem
                ))

                onDrawWithContent {
                    drawRoundRect(
                        brush = brush,
                        topLeft = Offset(
                            x = 0f,
                            y = size.height
                        ),
                        size = this.size.copy(height = height),
                        style = Fill,
                        cornerRadius = CornerRadius(x = 10f)
                    )
                }
            }
            .then(HealthChartData(graph[index]))
    )
}

internal class HealthChartData(
    val height: Long,
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any {
        return this@HealthChartData
    }
}

@Composable
fun StepGraphTail(
    item: String,
    textAlign: TextAlign,
) {
    val startPadding = if (item.length == 1) 3.dp else 0.dp

    DescriptionSmallText(
        text = item,
        modifier = Modifier.padding(start = startPadding),
        textAlign = textAlign,
    )
}

@Composable
fun StepGraphHeader(
    max: String,
    avg: String,
) {
    Column(modifier = Modifier.padding(end = 10.dp)) {
        Text(
            text = max,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
        Text(
            text = avg,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onBackground
        )
        Spacer(modifier = Modifier.weight(1f))
    }
}

@Composable
@Preview(widthDp = 320, heightDp = 300)
private fun PreviewStepBar() = StepMateTheme {
    val graph = run {
        mutableListOf<Long>().apply {
            repeat(24) { idx ->
                add(
                    1000 + idx.toLong() * 50
                )
            }
        }
    }
    val itemsCount = graph.size
    Row(
        modifier = Modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(horizontal = 5.dp),
    ) {
        repeat(itemsCount) { i ->
            StepBar(
                index = i,
                graph = graph,
                modifier = Modifier
                    .width(12.dp)
                    .height(300.dp),
                selectChartItem = {},
                barColor = listOf(
                    StepWalkColor.blue_700.color,
                    StepWalkColor.blue_600.color,
                    StepWalkColor.blue_500.color,
                    StepWalkColor.blue_400.color,
                    StepWalkColor.blue_300.color,
                    StepWalkColor.blue_200.color,
                )
            )
        }
    }
}