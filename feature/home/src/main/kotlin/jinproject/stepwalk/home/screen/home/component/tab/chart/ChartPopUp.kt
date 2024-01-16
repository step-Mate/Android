package jinproject.stepwalk.home.screen.home.component.tab.chart

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.center
import androidx.compose.ui.geometry.toRect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.home.HomeUiState
import jinproject.stepwalk.home.screen.home.HomeUiStatePreviewParameters
import jinproject.stepwalk.home.screen.home.component.PopUpState

@Composable
internal fun PopUp(
    modifier: Modifier = Modifier,
    popUpState: PopUpState,
    graph: List<Long>,
    barColor: List<Color>,
) {
    val popUpAnim by animateFloatAsState(
        targetValue = if (popUpState.enabled) 1f else 0f,
        label = "PopUpAnimateState",
    )
    val textMeasurer = rememberTextMeasurer()
    val textStyle = MaterialTheme.typography.bodySmall

    Spacer(
        modifier = modifier
            .drawWithCache {
                val stroke = Stroke(
                    width = 2.dp.toPx(),
                    pathEffect = PathEffect.cornerPathEffect(4.dp.toPx())
                )

                val brush = Brush.verticalGradient(
                    colors = barColor
                )
                val rect = size.toRect()
                val path = Path().apply {
                    moveTo(rect.topLeft.x, rect.topLeft.y)
                    lineTo(rect.bottomLeft.x, rect.bottomLeft.y)
                    lineTo(rect.bottomLeft.x, rect.bottomLeft.y + 15f)
                    lineTo(rect.bottomLeft.x + 20f, rect.bottomLeft.y)
                    lineTo(rect.bottomRight.x, rect.bottomRight.y)
                    lineTo(rect.topRight.x, rect.topRight.y)
                    lineTo(rect.topLeft.x, rect.topLeft.y)
                    close()
                }
                val fillPath = Path().apply {
                    addPath(path)
                    close()
                }

                val textResult = textMeasurer.measure(
                    text = if (popUpState.index >= 0) graph[popUpState.index].toString() else "",
                    style = textStyle
                )

                onDrawWithContent {
                    scale(popUpAnim) {
                        drawPath(path, brush = brush, style = stroke, alpha = popUpAnim)
                        drawPath(fillPath, brush = brush, style = Fill, alpha = popUpAnim)
                        drawText(
                            textResult,
                            topLeft = Offset(
                                x = size.center.x - textResult.size.width / 2,
                                y = size.center.y - textResult.size.height / 2
                            )
                        )
                    }
                }
            }
            .then(
                HealthPopUpData(
                    (graph.getOrNull(popUpState.index) ?: "").toString()
                )
            )
    )
}

@Composable
internal fun Modifier.addChartPopUpDismiss(
    popUpState: PopUpState,
    setPopUpState: (PopUpState) -> Unit,
) =
    this.pointerInput(popUpState.enabled) {
        awaitPointerEventScope {
            val event = awaitPointerEvent(PointerEventPass.Initial)
            if (event.type == PointerEventType.Press)
                setPopUpState(popUpState.copy(enabled = false))
        }
    }

@Stable
internal class HealthPopUpData(val figure: String) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any {
        return this@HealthPopUpData
    }

}

@Composable
@Preview(showBackground = true)
private fun PreviewChartPopUp(
    @PreviewParameter(HomeUiStatePreviewParameters::class, limit = 1)
    uiState: HomeUiState,
) = StepWalkTheme {
    Column(
        modifier = Modifier.size(200.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        PopUp(
            modifier = Modifier.size(40.dp),
            popUpState = PopUpState(enabled = true, index = 1),
            graph = uiState.step.graph,
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