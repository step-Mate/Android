package jinproject.stepwalk.home.calendar

import android.graphics.Color
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.calendar.component.Calendar
import jinproject.stepwalk.home.calendar.component.CalendarAppBar
import jinproject.stepwalk.home.screen.state.ZonedTime
import jinproject.stepwalk.home.screen.state.ZonedTimeRange
import jinproject.stepwalk.home.utils.onKorea
import jinproject.stepwalk.home.utils.toDayOfWeekString
import java.time.Instant
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun CalendarScreen(
    timeRange: ZonedTimeRange,
    popBackStack: () -> Unit,
) {
    val timeList = timeRange.toList()

    val pagerState = rememberPagerState(initialPage = timeList.size - 1) {
        timeList.size
    }
    val currentPage = timeList[pagerState.currentPage]

    DefaultLayout(
        contentPaddingValues = PaddingValues(top = 20.dp),
        topBar = {
            CalendarAppBar(
                time = currentPage.time,
                popBackStack = popBackStack
            )
        }
    ) {
        HorizontalPager(
            state = pagerState,
            modifier = Modifier.fillMaxWidth()
        ) { page ->
            Calendar(
                modifier = Modifier
                    .fillMaxWidth(),
                dayLabel = { dayOfWeek -> Label(dayOfWeek = dayOfWeek) },
                day = { day -> Day(day = day, time = timeList[page].time) }
            )
        }
    }
}

@Composable
private fun Label(
    dayOfWeek: Int
) {
    Text(
        text = dayOfWeek.toDayOfWeekString(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground,
        textAlign = TextAlign.Center,
        modifier = Modifier.padding(bottom = 30.dp)
    )
}

@Composable
private fun Day(
    day: Int,
    time: ZonedDateTime
) {
    val lastDayOnLastMonth = time
        .minusMonths(1L)
        .with(TemporalAdjusters.lastDayOfMonth())

    val weekOfLastDayOnLastMonth = lastDayOnLastMonth
        .dayOfWeek
        .value
        .toWeekFromSunToSat()

    val dayOfLastDayOnLastMonth = lastDayOnLastMonth
        .dayOfMonth

    val lastDayOfMonth = time
        .with(TemporalAdjusters.lastDayOfMonth())
        .dayOfMonth

    when {
        day <= weekOfLastDayOnLastMonth -> {
            Text(
                text = (day + (dayOfLastDayOnLastMonth - weekOfLastDayOnLastMonth)).toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.scrim,
                modifier = Modifier.height(40.dp),
                textAlign = TextAlign.Center
            )
        }

        day > lastDayOfMonth + weekOfLastDayOnLastMonth -> {
            Text(
                text = (day - (lastDayOfMonth + weekOfLastDayOnLastMonth)).toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.scrim,
                modifier = Modifier.height(40.dp),
                textAlign = TextAlign.Center
            )
        }

        else -> {
            Text(
                text = (day - weekOfLastDayOnLastMonth).toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.height(40.dp),
                textAlign = TextAlign.Center
            )
        }
    }
}

private fun Int.toWeekFromSunToSat(): Int =
    when (this) {
        7 -> 1
        6 -> 0
        else -> this + 1
    }

@Composable
@Preview(showBackground = true, backgroundColor = Color.WHITE.toLong())
private fun PreviewCalendarScreen() = StepWalkTheme {
    val today = ZonedTime(Instant.now().onKorea())
    CalendarScreen(
        timeRange = ZonedTimeRange(
            start = today.apply { time.minusMonths(10L) },
            endInclusive = today
        ),
        popBackStack = {}
    )
}