package com.stepmate.home.screen.calendar.component.chart

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
import com.stepmate.design.component.DescriptionLargeText
import com.stepmate.design.component.layout.chart.PopUpState
import com.stepmate.home.screen.home.component.tab.HealthChart
import com.stepmate.home.screen.home.state.Day
import com.stepmate.home.screen.home.state.Month
import com.stepmate.home.screen.home.state.Time
import com.stepmate.home.screen.home.state.Week
import com.stepmate.home.screen.home.state.Year
import kotlinx.collections.immutable.PersistentList

@Composable
internal fun ColumnScope.CalendarHealthChart(
    graph: PersistentList<Long>,
    header: String,
    type: Time,
    barColor: PersistentList<androidx.compose.ui.graphics.Color>,
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