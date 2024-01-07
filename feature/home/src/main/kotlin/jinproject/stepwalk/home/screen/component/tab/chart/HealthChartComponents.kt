package jinproject.stepwalk.home.screen.component.tab.chart

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
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.HomeUiState
import jinproject.stepwalk.home.screen.HomeUiStatePreviewParameters

@Composable
internal fun StepBar(
    index: Int,
    graph: List<Long>,
    modifier: Modifier = Modifier,
    setPopUpState: (Offset) -> Unit,
) {
    var height = 0f
    var offset = Offset.Zero

    Spacer(
        modifier = modifier
            .padding(horizontal = 2.dp)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                setPopUpState(
                    offset.copy(y = offset.y + height)
                )
            }
            .drawWithCache {
                val brush = Brush.verticalGradient(
                    colors = listOf(
                        StepWalkColor.blue_700.color,
                        StepWalkColor.blue_600.color,
                        StepWalkColor.blue_500.color,
                        StepWalkColor.blue_400.color,
                        StepWalkColor.blue_300.color,
                        StepWalkColor.blue_200.color,
                    )
                )

                val item = graph[index]
                val maxItem = graph.maxOrNull() ?: 0

                height = -(item.stepToSizeByMax(
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
            .onGloballyPositioned {
                offset = Offset(
                    x = it.positionInWindow().x,
                    y = it.boundsInWindow().bottom
                )
            }
    )
}

@Composable
internal fun StepGraphTail(
    item: String,
) {
    Text(
        text = item,
        textAlign = TextAlign.Left,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
internal fun StepGraphHeader(
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
private fun PreviewStepBar(
    @PreviewParameter(HomeUiStatePreviewParameters::class, limit = 1)
    homeUiState: HomeUiState,
) = StepWalkTheme {
    val graph = homeUiState.step.graph
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
                setPopUpState = {}
            )
        }
    }
}