package jinproject.stepwalk.home.component

import android.content.res.Configuration
import android.util.Log
import androidx.compose.foundation.layout.LayoutScopeMarker
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.ParentDataModifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.Density
import java.util.SortedMap
import kotlin.math.roundToInt

@Composable
internal fun PagerGraph(
    itemsCount: Int,
    modifier: Modifier = Modifier,
    horizontalAxis: @Composable (Int) -> Unit,
    verticalAxis: @Composable () -> Unit,
    bar: @Composable (Int) -> Unit,
    configuration: Configuration = LocalConfiguration.current
) {
    val bars = @Composable { repeat(itemsCount) { bar(it) } }
    val horizontalItems = @Composable {
        for (i in 0 until itemsCount step 2) {
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

        val verticalPlacable = verticalMeasurables.first().measure(constraints.toLoose())

        val itemWidth = (totalWidth - verticalPlacable.measuredWidth) / (itemsCount / 2)

        val horizontalPlacables = horizontalMeasurables.map { measurable ->
            measurable.measure(
                constraints.copy(
                    maxWidth = itemWidth,
                    minWidth = itemWidth,
                    minHeight = 0
                )
            )
        }

        val barPlaceables = barMeasurables.map { barMeasurable ->
            val barParentData = barMeasurable.parentData as GraphParentData
            val barHeight = (barParentData.value).roundToInt()

            val barPlacable = barMeasurable.measure(
                constraints.copy(
                    minHeight = barHeight,
                    maxHeight = barHeight,
                    minWidth = 0,
                )
            )

            barPlacable
        }

        layout(totalWidth, totalHeight) {
            var xPos = verticalPlacable.width

            verticalPlacable.place(0, totalHeight - barPlaceables.maxOf { it.height } - horizontalPlacables.first().height )

            barPlaceables.forEachIndexed { index, placeable ->

                if (index % 2 == 0) {
                    val horizontalPlaceable = horizontalPlacables[index / 2]
                    horizontalPlaceable.place(xPos, totalHeight - verticalPlacable.height)
                }

                when (index < 10) {
                    true -> {
                        placeable.place(
                            xPos,
                            totalHeight - verticalPlacable.height - placeable.height
                        )
                    }

                    false -> {
                        placeable.place(
                            xPos + 12,
                            totalHeight - verticalPlacable.height - placeable.height
                        )
                    }
                }

                xPos += horizontalPlacables.first().width / 2
            }
        }
    }
}

fun Constraints.toLoose() = this.copy(
    minWidth = 0,
    minHeight = 0
)

@LayoutScopeMarker
@Immutable
internal object GraphScope {
    @Stable
    fun Modifier.setGraphParentData(
        data: SortedMap<Int, Long>,
        key: Int,
        goal: Long
    ): Modifier {
        val max = data.values.max()
        return then(
            data[key]?.stepToDayGraphData(max) ?: GraphParentData(0f)
        )
    }

    private fun Long.stepToDayGraphData(max: Long) =
        when {
            this == 0L -> GraphParentData(
                value = 0f
            )

            this >= max -> GraphParentData(
                value = 400f
            )

            else -> GraphParentData(
                value = 400f / (max.toFloat() / this.toFloat())
            )
        }

    private fun Long.stepToWeekGraphData(max: Long, goal: Long) =
        when (max >= goal) {
            true -> {
                Log.d(
                    "test",
                    "1 max: $max goal: $goal value: ${400f / (max.toFloat() / this.toFloat())} goalOffset: ${400f / (max.toFloat() / goal.toFloat())}"
                )
                GraphParentData(
                    value = 400f / (max.toFloat() / this.toFloat()),
                    //goalOffset = 400f / (max.toFloat() / goal.toFloat())
                )
            }

            false -> {
                Log.d(
                    "test",
                    "2 max: $max goal: $goal value: ${300f / (goal.toFloat() / this.toFloat())}"
                )
                GraphParentData(
                    value = 300f / (goal.toFloat() / this.toFloat()),
                    //goalOffset = 300f / (goal.toFloat() / max.toFloat())
                )
            }
        }
}

internal class GraphParentData(
    val value: Float
) : ParentDataModifier {
    override fun Density.modifyParentData(parentData: Any?): Any? =
        this@GraphParentData
}