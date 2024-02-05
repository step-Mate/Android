package jinproject.stepwalk.home.screen.calendar.component.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.layout.chart.PopUpState
import jinproject.stepwalk.home.screen.home.component.tab.HealthChart
import jinproject.stepwalk.home.screen.home.state.Day
import jinproject.stepwalk.home.screen.home.state.Month
import jinproject.stepwalk.home.screen.home.state.Time
import jinproject.stepwalk.home.screen.home.state.Week
import jinproject.stepwalk.home.screen.home.state.Year

@Composable
internal fun ColumnScope.CalendarHealthChart(
    graph: List<Long>,
    header: String,
    type: Time,
    barColor: List<androidx.compose.ui.graphics.Color>,
    popUpState: PopUpState,
    setPopUpState: (PopUpState) -> Unit,
) {
    HealthChart(
        graph = graph,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
            .padding(10.dp),
        header = {
            DescriptionLargeText(
                text = when (type) {
                    Day -> "시간당 "
                    Month -> "일간 "
                    Year -> "월간 "
                    Week -> throw IllegalArgumentException("주간 달력은 존재하지 않음")
                } + header,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        },
        barColor = barColor,
        popUpState = popUpState,
        setPopUpState = setPopUpState,
    )
}