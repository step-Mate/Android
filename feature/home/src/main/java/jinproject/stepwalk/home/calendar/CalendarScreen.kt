package jinproject.stepwalk.home.calendar

import android.graphics.Color
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.utils.onKorea
import jinproject.stepwalk.home.utils.toDayOfWeekString
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.temporal.TemporalAdjusters

@Composable
internal fun CalendarScreen(
    popBackStack: () -> Unit
) {
    Calendar(
        modifier = Modifier.fillMaxSize(),
        header = { Header() },
        dayLabel = { dayOfWeek -> Label(dayOfWeek = dayOfWeek) },
        day = { day -> Day(day = day, month = LocalDateTime.now().onKorea().monthValue) }
    )
}

@Composable
private fun Header() {
    val today = LocalDateTime.now().onKorea()
    Row(
        modifier = Modifier.fillMaxWidth().wrapContentWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            painter = painterResource(id = jinproject.stepwalk.design.R.drawable.ic_time),
            contentDescription = "CalendarIcon"
        )
        Text(
            text = "${today.year}. ${today.monthValue}",
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
    month: Int
) {
    val lastDayOfWeekOnLastMonth = LocalDate
        .now()
        .withMonth(month)
        .minusMonths(1)
        .with(TemporalAdjusters.lastDayOfMonth())
        .dayOfWeek
        .value

    val lastDayOfMonth = LocalDate
        .now()
        .withMonth(month)
        .with(TemporalAdjusters.lastDayOfMonth())
        .dayOfMonth

    when {
        day <= lastDayOfWeekOnLastMonth + 1 -> {
            Text(
                text = day.toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.scrim
            )
        }
        day > lastDayOfMonth + lastDayOfWeekOnLastMonth + 1 -> {
            Text(
                text = (day - (lastDayOfMonth + lastDayOfWeekOnLastMonth + 1)).toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.scrim
            )
        }
        else -> {
            Text(
                text = (day - lastDayOfWeekOnLastMonth - 1).toString(),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = Color.WHITE.toLong())
private fun PreviewCalendarScreen() = StepWalkTheme {
    CalendarScreen(
        popBackStack = {}
    )
}