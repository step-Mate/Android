package jinproject.stepwalk.home.calendar

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.Layout
import jinproject.stepwalk.home.component.toLoose
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import kotlin.math.roundToInt

@Composable
internal fun Calendar(
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
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
        contents = listOf(header, dayLabels, days),
        modifier = modifier
    ) { (headerMeasurable, dayLabelMeasurables, dayMeasurables), constraints ->

        require(headerMeasurable.size == 1) {
            "Header composable 은 1개 이어야 함"
        }

        val maxWidth = constraints.maxWidth

        val headerPlaceable = headerMeasurable
            .first()
            .measure(constraints.toLoose())

        val dayLabelPlaceable = dayLabelMeasurables.map { measurable ->
            measurable.measure(
                constraints.copy(
                    maxWidth = (maxWidth / 7f).roundToInt(),
                    minWidth = (maxWidth / 7f).roundToInt(),
                    minHeight = 0
                )
            )
        }


        val dayPlaceable = dayMeasurables.map { measurable ->
            measurable.measure(
                constraints.copy(
                    maxWidth = (maxWidth / 7f).roundToInt(),
                    minWidth = (maxWidth / 7f).roundToInt(),
                    minHeight = 0
                )
            )
        }

        val totalHeight = headerPlaceable.height + dayLabelPlaceable.first().height + dayPlaceable.first().height * 6

        layout(maxWidth, totalHeight) {
            headerPlaceable.place(0, 0)

            var xPos = 0
            var yPos = headerPlaceable.height

            dayPlaceable.forEachIndexed { index, placeable ->
                if (index < 7)
                    dayLabelPlaceable[index].place(xPos, yPos)

                placeable.place(xPos, yPos + dayLabelPlaceable.first().height)

                xPos += placeable.width

                if ((index + 1) % 7 == 0) {
                    yPos += placeable.height
                    xPos = 0
                }

            }
        }
    }
}