package com.stepmate.home.screen.home.component.tab

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.stepmate.design.component.DefaultIconButton
import com.stepmate.design.component.DescriptionLargeText
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.design.component.layout.chart.HealthChartLayout
import com.stepmate.design.component.layout.chart.PopUpLtr
import com.stepmate.design.component.layout.chart.PopUpRtl
import com.stepmate.design.component.layout.chart.PopUpState
import com.stepmate.design.component.layout.chart.StepBar
import com.stepmate.design.component.layout.chart.StepGraphHeader
import com.stepmate.design.component.layout.chart.StepGraphTail
import com.stepmate.design.theme.StepWalkColor
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.home.screen.home.HomeUiState
import com.stepmate.home.screen.home.HomeUiStatePreviewParameters
import com.stepmate.home.screen.home.component.tab.menu.MenuPager
import com.stepmate.home.screen.home.state.Day
import com.stepmate.home.screen.home.state.HealthTab
import com.stepmate.home.screen.home.state.User
import com.stepmate.home.screen.home.state.Week
import com.stepmate.home.utils.displayOnKorea
import com.stepmate.home.utils.toDayOfWeekString
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.persistentListOf
import java.time.LocalDate

@Composable
internal fun ColumnScope.HealthTabLayout(
    healthTab: HealthTab,
    user: User,
    navigateToDetailChart: () -> Unit,
    popUpState: PopUpState,
    setPopUpState: (PopUpState) -> Unit,
) {
    MenuPager(
        healthTab = healthTab,
        user = user
    )

    VerticalSpacer(height = 40.dp)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant, RoundedCornerShape(20.dp))
            .padding(8.dp)
    ) {
        HealthChart(
            graph = healthTab.graph,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
                .padding(8.dp),
            header = {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    DescriptionLargeText(
                        text = "시간당 걸음수",
                        modifier = Modifier.align(Alignment.CenterStart),
                    )
                    DefaultIconButton(
                        modifier = Modifier.align(Alignment.CenterEnd),
                        icon = com.stepmate.design.R.drawable.ic_setting,
                        onClick = navigateToDetailChart,
                        iconTint = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            barColor = persistentListOf(
                StepWalkColor.blue_700.color,
                StepWalkColor.blue_600.color,
                StepWalkColor.blue_500.color,
                StepWalkColor.blue_400.color,
                StepWalkColor.blue_300.color,
                StepWalkColor.blue_200.color,
            ),
            popUpState = popUpState,
            setPopUpState = setPopUpState,
        )
    }
}

@Composable
internal fun ColumnScope.HealthChart(
    graph: PersistentList<Long>,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    barColor: PersistentList<Color>,
    popUpState: PopUpState,
    setPopUpState: (PopUpState) -> Unit,
) {
    HealthChartLayout(
        itemsCount = graph.size,
        modifier = modifier,
        horizontalAxis = { index ->
            StepGraphTail(
                item = when (graph.size) {
                    Week.toNumberOfDays() -> (index + 1).weekToString()
                    Day.toNumberOfDays() -> index.toString()
                    else -> (index + 1).toString()
                },
                textAlign = when (graph.size > 14) {
                    true -> TextAlign.Left
                    false -> TextAlign.Center
                },
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
                selectChartItem = { state ->
                    setPopUpState(
                        state
                    )
                },
                barColor = barColor
            )
        },
        header = header,
        popUp = {
            if (popUpState.index > graph.size / 2)
                PopUpRtl(
                    popUpState = popUpState,
                    graph = graph,
                    barColor = barColor
                )
            else
                PopUpLtr(
                    popUpState = popUpState,
                    graph = graph,
                    barColor = barColor
                )
        },
        popUpState = popUpState
    )
}

internal fun Int.weekToString() =
    when (val week = this.toDayOfWeekString()) {
        LocalDate.now().dayOfWeek.displayOnKorea() -> "오늘"

        else -> week
    }

@Composable
@Preview(showBackground = true)
private fun PreviewUserSteps(
    @PreviewParameter(HomeUiStatePreviewParameters::class)
    uiState: HomeUiState,
) = StepMateTheme {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        HealthTabLayout(
            healthTab = uiState.step,
            user = User.getInitValues(),
            navigateToDetailChart = {},
            popUpState = PopUpState.getInitValues(),
            setPopUpState = {}
        )
    }
}