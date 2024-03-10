package jinproject.stepwalk.home.screen.calendar.component.calendar

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.appendColorText
import jinproject.stepwalk.design.component.DefaultButton
import jinproject.stepwalk.design.component.DescriptionAnnotatedSmallText
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.home.screen.calendar.CalendarData
import jinproject.stepwalk.home.utils.toDayOfWeekString
import java.time.temporal.TemporalAdjusters

@Composable
internal fun Label(
    dayOfWeek: Int,
) {
    DescriptionLargeText(
        text = dayOfWeek.toDayOfWeekString(),
        modifier = Modifier
            .padding(bottom = 30.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
internal fun Day(
    day: Int,
    calendarData: CalendarData,
    setCalendarData: (CalendarData) -> Unit,

    ) {
    val lastDayOnLastMonth = calendarData.selectedTime
        .minusMonths(1L)
        .with(TemporalAdjusters.lastDayOfMonth())

    val weekOfLastDayOnLastMonth = lastDayOnLastMonth
        .dayOfWeek
        .value
        .toWeekFromSunToSat()

    val dayOfLastDayOnLastMonth = lastDayOnLastMonth
        .dayOfMonth

    val lastDayOfThisMonth = calendarData.selectedTime
        .with(TemporalAdjusters.lastDayOfMonth())
        .dayOfMonth

    var clickEnabled = false

    val text = when {
        day <= weekOfLastDayOnLastMonth -> {
            buildAnnotatedString {
                appendColorText(
                    text = (day + (dayOfLastDayOnLastMonth - weekOfLastDayOnLastMonth)).toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        day > lastDayOfThisMonth + weekOfLastDayOnLastMonth -> {
            buildAnnotatedString {
                appendColorText(
                    text = (day - (lastDayOfThisMonth + weekOfLastDayOnLastMonth)).toString(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }
        }

        else -> {
            clickEnabled = true
            val dayOnThisMonth = day - weekOfLastDayOnLastMonth
            val textColor =
                if (calendarData.selectedTime.dayOfMonth == dayOnThisMonth) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurface

            buildAnnotatedString {
                appendColorText(
                    text = dayOnThisMonth.toString(),
                    color = textColor,
                )
            }
        }
    }

    val backgroundColor =
        if (calendarData.selectedTime.dayOfMonth == text.text.toInt() && clickEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surface

    DefaultButton(
        onClick = {
            val newTime = calendarData.selectedTime.withDayOfMonth(text.text.toInt())

            setCalendarData(
                calendarData.copy(
                    type = jinproject.stepwalk.home.screen.home.state.Day,
                    selectedTime = newTime,
                )
            )
        },
        modifier = Modifier.height(30.dp),
        backgroundColor = backgroundColor,
        enabled = clickEnabled,
        contentPaddingValues = PaddingValues(horizontal = 12.dp, vertical = 8.dp)
    ) {
        DescriptionAnnotatedSmallText(
            text = text,
            modifier = Modifier,
            textAlign = TextAlign.Center,
        )
    }
}

private fun Int.toWeekFromSunToSat(): Int =
    when (this) {
        7 -> 1
        6 -> 0
        else -> this + 1
    }