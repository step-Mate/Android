package jinproject.stepwalk.home.component

import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
internal fun PagerGraph(
    itemsCount: Int,
    modifier: Modifier = Modifier,
    horizontalAxis: @Composable () -> Unit,
    verticalAxis: @Composable () -> Unit,
    bar: @Composable (Int) -> Unit
) {
    val bars = @Composable { repeat(itemsCount) { bar(it) } }

    Layout(
        contents = listOf(horizontalAxis, verticalAxis, bars),
        modifier = modifier
    ) { (horizontalMeasurables, verticalMeasurables, barMeasurables), constraints ->

        require(horizontalMeasurables.size == 1 && verticalMeasurables.size == 1) {
            "가로축과 세로축은 반드시 하나만 존재해야 함"
        }

        val horizontalPlacable = horizontalMeasurables.first().measure(constraints)
        val verticalPlacable = verticalMeasurables.first().measure(constraints)

        val totalWidth = horizontalPlacable.width + verticalPlacable.width

        val barPlacable = barMeasurables.map { barMeasurable ->
            val barParentData = barMeasurable.parentData as GraphParentData
            val barHeight = (barParentData.value).roundToInt()

            val barPlacable = barMeasurable.measure(
                constraints.copy(
                    minHeight = barHeight,
                    maxHeight = barHeight,
                )
            )

            barPlacable
        }

        val totalHeight = horizontalPlacable.height + barPlacable.maxOf { it.height }

        layout(totalWidth, totalHeight) {
            var xPos = verticalPlacable.width
            val yPos = verticalPlacable.height

            verticalPlacable.place(0, totalHeight - (yPos * 2))
            horizontalPlacable.place(xPos, totalHeight - yPos)

            barPlacable.forEach { placeable ->
                val barOffset = xPos + 4.dp.roundToPx()
                placeable.place(barOffset, totalHeight - yPos - placeable.height)
                xPos += placeable.width + 8.dp.roundToPx()
            }
        }
    }
}

@LayoutScopeMarker
@Immutable
internal object GraphScope {
    @Stable
    fun Modifier.setGraphParentData(
        value: Long,
        maxValue: Long
    ): Modifier {
        return then(
            GraphParentData(value.stepToGraphSize(maxValue))
        )
    }

    private fun Long.stepToGraphSize(max: Long) = if (this >= max) 300f else this / (max / 300f)
}

internal class GraphParentData(
    val value: Float
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any? =
        this@GraphParentData
}