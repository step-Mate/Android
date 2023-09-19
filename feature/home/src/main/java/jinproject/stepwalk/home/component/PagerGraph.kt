package jinproject.stepwalk.home.component

import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.Constraints

@Composable
internal fun PagerGraph(
    itemsCount: Int,
    modifier: Modifier = Modifier,
    horizontalAxis: @Composable (Int) -> Unit,
    verticalAxis: @Composable () -> Unit,
    bar: @Composable (Int) -> Unit
) {
    val bars = @Composable { repeat(itemsCount) { bar(it) } }

    val horizontalItemCount = if(itemsCount >= 16) itemsCount / 2 else itemsCount
    val horizontalStep = if(itemsCount == horizontalItemCount) 1 else 2
    val horizontalItems = @Composable {
        for (i in 0 until itemsCount step horizontalStep) {
            horizontalAxis(i)
        }
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
            constraints.toLoose()
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

        val barWidth = if(itemsCount == horizontalItemCount) itemWidth else itemWidth / 2

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

internal fun Constraints.toLoose() = this.copy(
    minWidth = 0,
    minHeight = 0
)

@Stable
internal fun Long.stepToSizeByMax(barHeight: Float, max: Long) =
    when {
        this == 0L -> 0f

        this >= max -> barHeight - 10f

        else -> (barHeight - 10f) / (max.toFloat() / this.toFloat())
    }