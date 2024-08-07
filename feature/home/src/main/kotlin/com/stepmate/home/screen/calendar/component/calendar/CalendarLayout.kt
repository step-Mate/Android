package com.stepmate.home.screen.calendar.component.calendar

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.home.screen.calendar.CalendarData
import com.stepmate.home.screen.calendar.state.CalendarPreviewData
import com.stepmate.home.screen.home.state.Day
import com.stepmate.home.screen.home.state.Month
import com.stepmate.home.screen.home.state.Week
import com.stepmate.home.screen.home.state.Year

@Composable
internal fun CalendarLayout(
    calendarData: CalendarData,
    setCalendarData: (CalendarData) -> Unit,
) {
    Crossfade(targetState = calendarData.type, label = "calendarCrossFade") { screen ->
        when (screen) {
            Day -> {
                CalendarDayLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .pointerInput(calendarData.selectedTime.monthValue) {
                            detectHorizontalDragGestures { change, dragAmount ->
                                val next = if (dragAmount > 0f)
                                    calendarData.selectedTime.minusMonths(
                                        1L
                                    )
                                else
                                    calendarData.selectedTime.plusMonths(
                                        1L
                                    )

                                calendarData.select(next) { time ->
                                    setCalendarData(
                                        calendarData.copy(
                                            selectedTime = time,
                                        )
                                    )
                                }
                            }
                        },
                    dayLabel = { dayOfWeek ->
                        Label(dayOfWeek = dayOfWeek)
                    },
                    day = { day ->
                        Day(
                            day = day,
                            calendarData = calendarData,
                            setCalendarData = setCalendarData,
                        )
                    },
                )
            }

            Month -> {
                Month(
                    calendarData = calendarData,
                    setCalendarData = setCalendarData,
                )
            }

            Year -> {
                Year(
                    calendarData = calendarData,
                    setCalendarData = setCalendarData
                )
            }

            Week -> {}
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewCalendarLayout(
    @PreviewParameter(CalendarPreviewData::class)
    calendarData: CalendarData,
) = StepMateTheme {
    CalendarLayout(
        calendarData = calendarData,
        setCalendarData = {}
    )
}