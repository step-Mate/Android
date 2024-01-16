package jinproject.stepwalk.home.screen.calendar.component.calendar

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowColumn
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.DefaultButton
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.calendar.CalendarData
import jinproject.stepwalk.home.screen.calendar.state.CalendarPreviewData

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun Year(
    calendarData: CalendarData,
    setCalendarData: (CalendarData) -> Unit,
) {
    FlowColumn(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        calendarData.range
            .distinctBy {
                it.time.year
            }.forEach { zonedTime ->
                val year = zonedTime.time.year
                val backgroundColor = if (calendarData.selectedTime.year == year)
                    MaterialTheme.colorScheme.primary
                else
                    MaterialTheme.colorScheme.secondary

                DefaultButton(
                    onClick = {
                        setCalendarData(
                            calendarData.copy(
                                selectedTime = calendarData.selectedTime
                                    .withYear(year),
                            )
                        )
                    },
                    modifier = Modifier
                        .height(50.dp)
                        .fillMaxWidth()
                        .padding(vertical = 8.dp),
                    backgroundColor = backgroundColor
                ) {
                    DescriptionLargeText(
                        text = year.toString(),
                        modifier = Modifier,
                        textAlign = TextAlign.Center,
                    )
                }
            }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewCalendarYear(
) = StepWalkTheme {
    Year(
        calendarData = CalendarPreviewData.year,
        setCalendarData = {}
    )
}