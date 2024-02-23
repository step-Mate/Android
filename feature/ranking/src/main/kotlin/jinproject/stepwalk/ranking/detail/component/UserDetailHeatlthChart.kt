package jinproject.stepwalk.ranking.detail.component

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.layout.chart.HealthChartLayout
import jinproject.stepwalk.design.component.layout.chart.PopUp
import jinproject.stepwalk.design.component.layout.chart.PopUpState
import jinproject.stepwalk.design.component.layout.chart.StepBar
import jinproject.stepwalk.design.component.layout.chart.StepGraphHeader
import jinproject.stepwalk.design.component.layout.chart.StepGraphTail
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.ranking.detail.User
import jinproject.stepwalk.ranking.detail.UserDetailPreviewParameter

@Composable
internal fun ColumnScope.UserDetailHealthChart(
    graph: List<Long>,
    header: String,
    barColor: List<Color>,
    popUpState: PopUpState,
    setPopUpState: (PopUpState) -> Unit,
) {
    HealthChart(
        graph = graph,
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .padding(10.dp),
        header = {
            DescriptionLargeText(
                text = "월간 $header",
                modifier = Modifier.padding(vertical = 10.dp)
            )
        },
        barColor = barColor,
        popUpState = popUpState,
        setPopUpState = setPopUpState,
    )
}

@Composable
private fun ColumnScope.HealthChart(
    graph: List<Long>,
    modifier: Modifier = Modifier,
    header: @Composable () -> Unit,
    barColor: List<Color>,
    popUpState: PopUpState,
    setPopUpState: (PopUpState) -> Unit,
) {
    HealthChartLayout(
        itemsCount = graph.size,
        modifier = modifier,
        horizontalAxis = { index ->
            StepGraphTail(
                item = (index + 1).toString(),
                textAlign = TextAlign.Left,
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
            PopUp(
                popUpState = popUpState,
                graph = graph,
                barColor = barColor
            )
        },
        popUpState = popUpState
    )
}

@Composable
@Preview(showBackground = true)
private fun PreviewUserDetailHealthChart(
    @PreviewParameter(UserDetailPreviewParameter::class)
    user: User,
) = StepWalkTheme {
    var popUpState = PopUpState.getInitValues()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.background)
    ) {
        UserDetailHealthChart(
            graph = user.steps,
            header = "걸음수",
            barColor = listOf(
                StepWalkColor.blue_700.color,
                StepWalkColor.blue_600.color,
                StepWalkColor.blue_500.color,
                StepWalkColor.blue_400.color,
                StepWalkColor.blue_300.color,
                StepWalkColor.blue_200.color,
            ),
            popUpState = popUpState,
            setPopUpState = { state -> popUpState = state },
        )
    }
}