package jinproject.stepwalk.home.calendar.component

import android.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.TitleAppBar
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.state.ZonedTime
import jinproject.stepwalk.home.utils.onKorea
import java.time.Instant
import java.time.ZonedDateTime

@Composable
internal fun CalendarAppBar(
    time: ZonedDateTime,
    popBackStack: () -> Unit,
) {
    TitleAppBar(
        title = "${time.year}. ${time.monthValue}",
        startIcon = R.drawable.ic_arrow_left_small,
        onBackClick = popBackStack
    )
}

@Composable
@Preview(showBackground = true, backgroundColor = Color.WHITE.toLong())
private fun PreviewCalendarAppBar() = StepWalkTheme {
    val today = ZonedTime(Instant.now().onKorea())
    CalendarAppBar(
        time = today.time,
        popBackStack = {}
    )
}