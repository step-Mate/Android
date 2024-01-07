package jinproject.stepwalk.home.screen.component.tab.chart

import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints
import jinproject.stepwalk.design.component.asLoose
import jinproject.stepwalk.home.screen.state.sortDayOfWeek

@Composable
internal fun HealthChartLayout(
    itemsCount: Int,
    modifier: Modifier = Modifier,
    horizontalAxis: @Composable (Int) -> Unit,
    verticalAxis: @Composable () -> Unit,
    bar: @Composable (Int) -> Unit
) {
    val bars = @Composable { repeat(itemsCount) { bar(it) } }

    val horizontalItemCount = if (itemsCount >= 16) itemsCount / 2 else itemsCount
    val horizontalStep = if (itemsCount == horizontalItemCount) 1 else 2
    val horizontalItemList = (0 until itemsCount step horizontalStep).toList()
    val horizontalItemConversion =
        if (itemsCount == 7) horizontalItemList.sortDayOfWeek() else horizontalItemList
    val horizontalItems = @Composable {
        horizontalItemConversion.forEach { h -> horizontalAxis(h) }
    }

    Layout(
        contents = listOf(horizontalItems, verticalAxis, bars),
        modifier = modifier
    ) { (horizontalMeasurables, verticalMeasurables, barMeasurables), constraints ->

        require(verticalMeasurables.size == 1) {
            "세로축은 반드시 하나만 존재해야 함"
        }
        val totalHeight = constraints.maxHeight
        val totalWidth = constraints.maxWidth

        val verticalPlacable = verticalMeasurables.first().measure(
            constraints.asLoose()
        )

        val itemWidth = (totalWidth - verticalPlacable.width) / horizontalItemCount

        val horizontalPlacables = horizontalMeasurables.map { measurable ->
            measurable.measure(
                constraints.copy(
                    maxWidth = itemWidth,
                    minWidth = itemWidth,
                    minHeight = 0
                )
            )
        }

        val barWidth = if (itemsCount == horizontalItemCount) itemWidth else itemWidth / 2

        val barPlaceables = barMeasurables.map { barMeasurable ->
            val barHeight = totalHeight - horizontalPlacables.first().height

            barMeasurable.measure(
                constraints.copy(
                    minHeight = barHeight,
                    maxHeight = barHeight,
                    minWidth = barWidth,
                    maxWidth = barWidth
                )
            )
        }

        layout(totalWidth, totalHeight) {
            var xPos = verticalPlacable.width

            verticalPlacable.place(
                0,
                0
            )

            barPlaceables.forEachIndexed { index, placeable ->

                if (index % 2 == 0 || itemsCount == horizontalItemCount) {
                    val horizontalPlaceable = horizontalPlacables[index / horizontalStep]
                    horizontalPlaceable.place(xPos, totalHeight - horizontalPlaceable.height)
                }

                placeable.place(
                    xPos,
                    totalHeight - horizontalPlacables.first().height - placeable.height
                )

                xPos += horizontalPlacables.first().width / horizontalStep
            }
        }
    }
}

@Stable
internal fun Long.stepToSizeByMax(barHeight: Float, max: Long) =
    when {
        this == 0L -> 0f

        this >= max -> barHeight

        else -> barHeight / (max.toFloat() / this.toFloat())
    }