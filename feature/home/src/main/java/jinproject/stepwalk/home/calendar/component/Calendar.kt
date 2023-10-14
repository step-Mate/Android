package jinproject.stepwalk.home.calendar.component

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
internal fun Calendar(
    modifier: Modifier = Modifier,
    dayLabel: @Composable (Int) -> Unit,
    day: @Composable (Int) -> Unit,
) {
    val days = @Composable { repeat(42) { day(it + 1) } }
    val dayLabels = @Composable {
        repeat(7) {
            val dayOfWeek = when (it) {
                0 -> 7
                else -> it
            }
            dayLabel(dayOfWeek)
        }
    }

    Layout(
        contents = listOf(dayLabels, days),
        modifier = modifier
    ) { (dayLabelMeasurables, dayMeasurables), constraints ->

        val maxWidth = constraints.maxWidth

        val dayLabelPlaceable = dayLabelMeasurables.map { measurable ->
            measurable.measure(
                constraints.copy(
                    maxWidth = ((maxWidth - 16.dp.roundToPx()) / 7f).roundToInt(),
                    minWidth = ((maxWidth - 16.dp.roundToPx()) / 7f).roundToInt(),
                    minHeight = 0
                )
            )
        }


        val dayPlaceable = dayMeasurables.map { measurable ->
            measurable.measure(
                constraints.copy(
                    maxWidth = ((maxWidth - 16.dp.roundToPx()) / 7f).roundToInt(),
                    minWidth = ((maxWidth - 16.dp.roundToPx()) / 7f).roundToInt(),
                    minHeight = 0
                )
            )
        }

        val totalHeight = dayLabelPlaceable.first().height + dayPlaceable.first().height * 6

        layout(maxWidth, totalHeight) {

            var xPos = 8.dp.roundToPx()
            var yPos = 0

            dayPlaceable.forEachIndexed { index, placeable ->
                if (index < 7)
                    dayLabelPlaceable[index].place(xPos, yPos)

                placeable.place(xPos, yPos + dayLabelPlaceable.first().height)

                xPos += placeable.width

                if ((index + 1) % 7 == 0) {
                    yPos += placeable.height
                    xPos = 8.dp.roundToPx()
                }

            }
        }
    }
}