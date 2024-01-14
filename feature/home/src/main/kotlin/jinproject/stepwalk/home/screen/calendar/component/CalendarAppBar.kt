package jinproject.stepwalk.home.screen.calendar.component

import android.graphics.Color
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.StepMateTitleTopBar
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.calendar.CalendarData
import jinproject.stepwalk.home.screen.home.state.Day
import jinproject.stepwalk.home.screen.home.state.Month
import jinproject.stepwalk.home.screen.home.state.Time
import jinproject.stepwalk.home.screen.home.state.Week
import jinproject.stepwalk.home.screen.home.state.Year

@Composable
internal fun CalendarAppBar(
    calendarData: CalendarData,
    popBackStack: () -> Unit,
    onDateChange: (Time) -> Unit,
    content: @Composable BoxScope.() -> Unit = {},
) {
    val time = calendarData.selectedTime
    val text = when (calendarData.type) {
        Day -> "${time.year}년 ${time.monthValue}월"
        Month -> "${time.year}년"
        Year -> "년도를 선택해 주세요."
        Week -> throw IllegalArgumentException("주단위 달력은 존재 하지 않음.")
    }
    val prevType = when (calendarData.type) {
        Day -> Month
        Month -> Year
        Year -> Year
        Week -> throw IllegalArgumentException("${calendarData.type} 에서는 전환 불가. 년도 까지만 전환 가능")
    }
    StepMateTitleTopBar(
        modifier = Modifier.clickable(enabled = calendarData.type == Day || calendarData.type == Month) {
            onDateChange(prevType)
        },
        text = text,
        icon = R.drawable.ic_arrow_left_small,
        onClick = popBackStack,
        content = content,
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = Color.WHITE.toLong())
private fun PreviewCalendarAppBar() = StepWalkTheme {
    CalendarAppBar(
        calendarData = CalendarData.getInitValues(),
        popBackStack = {},
        onDateChange = {},
    )
}