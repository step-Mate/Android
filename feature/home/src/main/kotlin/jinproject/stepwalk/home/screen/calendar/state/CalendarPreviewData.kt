package jinproject.stepwalk.home.screen.calendar.state

import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import jinproject.stepwalk.home.screen.calendar.CalendarData
import jinproject.stepwalk.home.screen.home.state.Month
import jinproject.stepwalk.home.screen.home.state.Year
import java.time.ZonedDateTime

internal class CalendarPreviewData : PreviewParameterProvider<CalendarData> {
    private val today = ZonedDateTime.now()
    val month = CalendarData(
        type = Month,
        range = ZonedTime(today.withMonth(1))..ZonedTime(today.withMonth(12)),
        selectedTime = today,
    )
    val year = CalendarData(
        type = Year,
        range = ZonedTime(today.minusYears(2L))..ZonedTime(today),
        selectedTime = today,
    )

    override val values: Sequence<CalendarData> = sequenceOf(
        CalendarData.getInitValues(),
        month,
        year
    )
}