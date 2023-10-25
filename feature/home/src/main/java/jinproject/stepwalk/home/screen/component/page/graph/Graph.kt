package jinproject.stepwalk.home.screen.component.page.graph

import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.theme.StepWalkColor

@Composable
fun StepBar(
    index: Int,
    item: Long,
    nextItem: Long,
    maxItem: Long,
    horizontalSize: Int,
    modifier: Modifier = Modifier,
    setSelectedStepOnGraph: (Int, Long) -> Unit,
    setPopUpState: () -> Unit,
    setPopUpOffset: (Offset) -> Unit,
) {
    val clickState = remember {
        mutableStateOf(false)
    }
    val height = rememberSaveable {
        mutableFloatStateOf(0f)
    }
    Spacer(
        modifier = modifier
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                setSelectedStepOnGraph(index, item)
                setPopUpState()
                clickState.value = true
            }
            .onGloballyPositioned {
                if (clickState.value) {
                    setPopUpOffset(
                        Offset(
                            x = it.positionInWindow().x,
                            y = it.positionInWindow().y + height.floatValue
                        )
                    )
                    clickState.value = false
                }
            }
            .drawWithCache {
                val stroke = Stroke(2.dp.toPx())
                val brush = Brush.verticalGradient(
                    colors = listOf(
                        StepWalkColor.blue_700.color,
                        StepWalkColor.blue_600.color,
                        StepWalkColor.blue_500.color,
                        StepWalkColor.blue_400.color,
                        StepWalkColor.blue_300.color
                    )
                )
                val fillBrush = Brush.verticalGradient(
                    colors = listOf(
                        StepWalkColor.blue_700.color.copy(alpha = 0.8f),
                        StepWalkColor.blue_400.color.copy(alpha = 0.6f),
                        Color.Transparent
                    )
                )

                height.floatValue = this@drawWithCache.size.height - item.stepToSizeByMax(
                    barHeight = this@drawWithCache.size.height,
                    max = maxItem
                )

                val path = Path().apply {
                    moveTo(
                        x = 10f,
                        y = this@drawWithCache.size.height - item.stepToSizeByMax(
                            barHeight = this@drawWithCache.size.height,
                            max = maxItem
                        )
                    )
                    if (index < horizontalSize - 1) {
                        lineTo(
                            x = this@drawWithCache.size.width + 10f,
                            y = this@drawWithCache.size.height - nextItem.stepToSizeByMax(
                                barHeight = this@drawWithCache.size.height,
                                max = maxItem
                            )
                        )
                    }
                }

                val filledPath = Path().apply {
                    addPath(path)
                    if (index < horizontalSize - 1) {
                        lineTo(
                            x = this@drawWithCache.size.width + 10f,
                            y = this@drawWithCache.size.height
                        )
                        lineTo(
                            x = 10f,
                            y = this@drawWithCache.size.height
                        )
                    }
                    close()
                }

                onDrawBehind {
                    drawPath(path, brush, style = stroke)
                    drawPath(filledPath, fillBrush, style = Fill)
                    drawCircle(
                        brush,
                        radius = 10f,
                        center = Offset(
                            x = 10f,
                            y = this.size.height - item.stepToSizeByMax(
                                barHeight = this@drawWithCache.size.height,
                                max = maxItem
                            )
                        )
                    )
                }
            }
    )
}

@Composable
fun StepGraphTail(
    item: String
) {
    Text(
        text = item,
        textAlign = TextAlign.Left,
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
fun StepGraphHeader(
    max: String,
    avg: String
) {
    Column {
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