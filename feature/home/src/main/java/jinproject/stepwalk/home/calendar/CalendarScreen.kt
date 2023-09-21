package jinproject.stepwalk.home.calendar

import android.graphics.Color
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.state.ZonedTime
import jinproject.stepwalk.home.state.ZonedTimeRange
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

    HorizontalPager(
        state = pagerState
    ) {
        Calendar(
            modifier = Modifier
                .fillMaxWidth(),
            header = { Header(time = currentPage.time) },
            dayLabel = { dayOfWeek -> Label(dayOfWeek = dayOfWeek) },
            day = { day -> Day(day = day, time = currentPage.time) }
        )
    }
}

@Composable
private fun Header(
    time: ZonedDateTime
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = jinproject.stepwalk.design.R.drawable.ic_time),
            contentDescription = "CalendarIcon"
        )
        Text(
            text = "${time.year}. ${time.monthValue}",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Composable
private fun Label(
    dayOfWeek: Int
) {
    Text(
        text = dayOfWeek.toDayOfWeekString(),
        style = MaterialTheme.typography.bodyMedium,
        color = MaterialTheme.colorScheme.onBackground
    )
}

@Composable
private fun Day(
    day: Int,
    time: ZonedDateTime
) {
    val lastDayOfWeekOnLastMonth = time
        .minusMonths(1)
        .with(TemporalAdjusters.lastDayOfMonth())
        .dayOfWeek
        .value

    val lastDayOfMonth = time
        .with(TemporalAdjusters.lastDayOfMonth())
        .dayOfMonth

    when {
        day <= lastDayOfWeekOnLastMonth + 1 -> {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.scrim,
                modifier = Modifier.height(40.dp)
            )
        }

        day > lastDayOfMonth + lastDayOfWeekOnLastMonth + 1 -> {
            Text(
                text = (day - (lastDayOfMonth + lastDayOfWeekOnLastMonth + 1)).toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.scrim,
                modifier = Modifier.height(40.dp)
            )
        }

        else -> {
            Text(
                text = (day - lastDayOfWeekOnLastMonth - 1).toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.height(40.dp)
            )
        }
    }
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