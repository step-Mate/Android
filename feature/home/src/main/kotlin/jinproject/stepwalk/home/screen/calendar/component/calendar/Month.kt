package jinproject.stepwalk.home.screen.calendar.component.calendar

import android.content.res.Configuration
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.DefaultButton
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.calendar.CalendarData
import jinproject.stepwalk.home.screen.calendar.state.CalendarPreviewData

@OptIn(ExperimentalLayoutApi::class)
@Composable
internal fun Month(
    configuration: Configuration = LocalConfiguration.current,
    calendarData: CalendarData,
    setCalendarData: (CalendarData) -> Unit,
) {
    val width = (configuration.screenWidthDp / 4).dp

    FlowRow(
        modifier = Modifier.fillMaxWidth(),
        maxItemsInEachRow = 3,
        horizontalArrangement = Arrangement.SpaceAround,
    ) {
        calendarData.range.filter {
            it.time.year == calendarData.selectedTime.year
        }.forEach { zonedTime ->
            val month = zonedTime.time.monthValue
            val backgroundColor =
                if (calendarData.selectedTime.monthValue == month) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary

            DefaultButton(
                onClick = {
                    setCalendarData(
                        calendarData.copy(
                            selectedTime = calendarData.selectedTime.withMonth(month),
                        )
                    )
                },
                modifier = Modifier
                    .width(width)
                    .padding(vertical = 8.dp),
                backgroundColor = backgroundColor,
            ) {
                DescriptionLargeText(
                    text = month.toString() + "월",
                    modifier = Modifier,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewCalendarMonth(
) = StepWalkTheme {
    Month(
        calendarData = CalendarPreviewData.month,
        setCalendarData = {}
    )
}