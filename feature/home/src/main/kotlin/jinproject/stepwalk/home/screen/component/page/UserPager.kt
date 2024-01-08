package jinproject.stepwalk.home.screen.component.page

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.PreviewStepWalkTheme
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.home.screen.HomeUiState
import jinproject.stepwalk.home.screen.HomeUiStatePreviewParameters
import jinproject.stepwalk.home.screen.component.PopUpState
import jinproject.stepwalk.home.screen.component.GraphPopup
import jinproject.stepwalk.home.screen.component.page.graph.HealthGraph
import jinproject.stepwalk.home.screen.component.page.graph.StepBar
import jinproject.stepwalk.home.screen.component.page.graph.StepGraphHeader
import jinproject.stepwalk.home.screen.component.page.graph.StepGraphTail
import jinproject.stepwalk.home.screen.component.page.menu.MenuDetails
import jinproject.stepwalk.home.screen.component.page.pager.MenuPager
import jinproject.stepwalk.home.screen.state.Day
import jinproject.stepwalk.home.screen.state.HealthTab
import jinproject.stepwalk.home.screen.state.Week
import jinproject.stepwalk.home.utils.displayOnKorea
import jinproject.stepwalk.home.utils.toDayOfWeekString
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun UserPager(
    uiState: HomeUiState,
    modifier: Modifier = Modifier,
) {
    val pages = listOf(uiState.step, uiState.heartRate)

    val pagerState = rememberPagerState(initialPage = Int.MAX_VALUE / 2) {
        Integer.MAX_VALUE
    }

    PageMenu(
        pages = pages,
        pagerState = pagerState,
        modifier = modifier,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun PageMenu(
    modifier: Modifier = Modifier,
    pages: List<HealthTab>,
    pagerState: PagerState,
) {
    val currentPage = pages[pagerState.currentPage % pages.size]

    Column(
        modifier = modifier
    ) {
        MenuPager(
            pages = pages,
            pagerState = pagerState
        )

        VerticalSpacer(height = 20.dp)

        MenuDetails(
            menuList = currentPage.menu
        )

        VerticalSpacer(height = 40.dp)

        val graph = currentPage.graph
        var popUpState by remember { mutableStateOf(PopUpState.getInitValues()) }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentHeight()
                .clip(RoundedCornerShape(10.dp))
                .background(MaterialTheme.colorScheme.secondary)
                .padding(start = 10.dp, end = 10.dp, bottom = 10.dp, top = 10.dp)
        ) {
            HealthGraph(
                itemsCount = graph.size,
                modifier = Modifier
                    .fillMaxSize()
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
    homeUiState: HomeUiState
) = PreviewStepWalkTheme {
    UserPager(
        uiState = homeUiState,
    )
}