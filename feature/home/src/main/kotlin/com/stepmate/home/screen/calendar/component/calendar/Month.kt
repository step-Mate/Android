package com.stepmate.home.screen.calendar.component.calendar

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
import androidx.compose.ui.unit.dp
import com.stepmate.design.component.DefaultButton
import com.stepmate.design.component.DescriptionLargeText
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.home.screen.calendar.CalendarData
import com.stepmate.home.screen.calendar.state.CalendarPreviewData

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
        calendarData.range.filter { zonedTime ->
            zonedTime.time.year == calendarData.selectedTime.year
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
) = StepMateTheme {
    Month(
        calendarData = CalendarPreviewData().month,
        setCalendarData = {}
    )
}