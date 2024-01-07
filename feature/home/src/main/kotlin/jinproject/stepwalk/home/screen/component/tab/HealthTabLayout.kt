package jinproject.stepwalk.home.screen.component.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.PreviewStepWalkTheme
import jinproject.stepwalk.design.component.DefaultIconButton
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.home.screen.HomeUiState
import jinproject.stepwalk.home.screen.HomeUiStatePreviewParameters
import jinproject.stepwalk.home.screen.component.GraphPopup
import jinproject.stepwalk.home.screen.component.PopUpState
import jinproject.stepwalk.home.screen.component.tab.chart.HealthChartLayout
import jinproject.stepwalk.home.screen.component.tab.chart.StepBar
import jinproject.stepwalk.home.screen.component.tab.chart.StepGraphHeader
import jinproject.stepwalk.home.screen.component.tab.chart.StepGraphTail
import jinproject.stepwalk.home.screen.component.tab.menu.MenuPager
import jinproject.stepwalk.home.screen.state.Day
import jinproject.stepwalk.home.screen.state.HealthTab
import jinproject.stepwalk.home.screen.state.Week
import jinproject.stepwalk.home.utils.displayOnKorea
import jinproject.stepwalk.home.utils.toDayOfWeekString
import java.time.LocalDate

@Composable
internal fun HealthTabLayout(
    healthTab: HealthTab,
    navigateToDetailChart: () -> Unit,
) {
    MenuPager(healthTab = healthTab,)

    VerticalSpacer(height = 40.dp)

    val graph = healthTab.graph
    var popUpState by remember { mutableStateOf(PopUpState.getInitValues()) }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(10.dp))
            .background(MaterialTheme.colorScheme.secondary)
            .padding(10.dp)
    ) {
        DefaultIconButton(
            modifier = Modifier.align(Alignment.End),
            icon = jinproject.stepwalk.design.R.drawable.ic_setting,
            onClick = navigateToDetailChart,
            iconTint = MaterialTheme.colorScheme.onBackground
        )
        HealthChartLayout(
            itemsCount = graph.size,
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.background)
                .padding(10.dp),
            horizontalAxis = { index ->
                StepGraphTail(
                    item = when (graph.size) {
                        Week.toNumberOfDays() -> (index + 1).weekToString()
                        Day.toNumberOfDays() -> index.toString()
                        else -> (index + 1).toString()
                    }
                )
            },
            verticalAxis = {
                val graphVerticalMax = graph.maxOrNull() ?: 0

                StepGraphHeader(
                    max = graphVerticalMax.toString(),
                    avg = (graphVerticalMax / 2).toString()
                )
            },
            bar = { index ->
                StepBar(
                    index = index,
                    graph = graph,
                    setPopUpState = { offset ->
                        popUpState = PopUpState(
                            state = true,
                            offset = offset,
                            message = graph[index].toString()
                        )
                    }
                )
            }
        )

        GraphPopup(
            popUpState = popUpState,
            offPopUp = { popUpState = PopUpState.getInitValues() }
        )
    }
}

private fun Int.weekToString() =
    when (val week = this.toDayOfWeekString()) {
        LocalDate.now().dayOfWeek.displayOnKorea() -> "오늘"

        else -> week
    }

@Composable
@Preview
private fun PreviewUserSteps(
    @PreviewParameter(HomeUiStatePreviewParameters::class)
    uiState: HomeUiState,
) = PreviewStepWalkTheme {
    Column(modifier = Modifier.fillMaxSize()) {
        HealthTabLayout(
            healthTab = uiState.step,
            navigateToDetailChart = {}
        )
    }
}