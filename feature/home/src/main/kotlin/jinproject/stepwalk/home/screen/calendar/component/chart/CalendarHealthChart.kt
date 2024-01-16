package jinproject.stepwalk.home.screen.calendar.component.chart

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.home.screen.home.component.PopUpState
import jinproject.stepwalk.home.screen.home.component.tab.HealthChart

@Composable
internal fun ColumnScope.CalendarHealthChart(
    graph: List<Long>,
    header: String,
    barColor: List<androidx.compose.ui.graphics.Color>,
    popUpState: PopUpState,
    setPopUpState: (PopUpState) -> Unit,
) {
    HealthChart(
        graph = graph,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .clip(RoundedCornerShape(20.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(10.dp),
        header = {
            DescriptionLargeText(
                text = header,
                modifier = Modifier.padding(vertical = 10.dp)
            )
        },
        barColor = barColor,
        popUpState = popUpState,
        setPopUpState = setPopUpState,
    )
}