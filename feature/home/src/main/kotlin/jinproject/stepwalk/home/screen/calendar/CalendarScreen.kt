package jinproject.stepwalk.home.screen.calendar

import android.graphics.Color
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.StepMateProgressIndicator
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.component.VerticalWeightSpacer
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.calendar.component.CalendarAppBar
import jinproject.stepwalk.home.screen.calendar.component.calendar.CalendarLayout
import jinproject.stepwalk.home.screen.calendar.component.chart.CalendarHealthChart
import jinproject.stepwalk.home.screen.home.HomeUiState
import jinproject.stepwalk.home.screen.home.HomeUiStatePreviewParameters
import jinproject.stepwalk.home.screen.home.component.PopUpState
import jinproject.stepwalk.home.screen.home.component.tab.chart.addChartPopUpDismiss
import jinproject.stepwalk.home.screen.home.state.CaloriesMenuFactory
import jinproject.stepwalk.home.screen.home.state.Day
import jinproject.stepwalk.home.screen.home.state.SnackBarMessage
import jinproject.stepwalk.home.screen.home.state.TimeMenuFactory
import kotlin.math.ceil
import kotlin.math.roundToLong

@Composable
internal fun CalendarScreen(
    calendarViewModel: CalendarViewModel = hiltViewModel(),
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    val calendarData by calendarViewModel.calendarData.collectAsStateWithLifecycle()
    val uiState by calendarViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = calendarData) {
        when (calendarData.type) {
            Day -> {
                calendarViewModel::setDaySteps.invoke()
            }

            else -> {
                calendarViewModel::setPeriodSteps.invoke()
            }
        }
    }

    CalendarScreen(
        uiState = uiState,
        calendarData = calendarData,
        popBackStack = popBackStack,
        setCalendarData = calendarViewModel::setCalendarData,
        showSnackBar = showSnackBar,
    )
}

@Composable
internal fun CalendarScreen(
    uiState: CalendarViewModel.UiState,
    calendarData: CalendarData,
    popBackStack: () -> Unit,
    setCalendarData: (CalendarData) -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    when (uiState) {
        CalendarViewModel.UiState.Loading -> {
            StepMateProgressIndicator()
        }

        is CalendarViewModel.UiState.Success -> {
            OnSuccessCalendarScreen(
                uiState = uiState,
                calendarData = calendarData,
                popBackStack = popBackStack,
                setCalendarData = setCalendarData,
            )
        }

        is CalendarViewModel.UiState.Error -> {
            showSnackBar(
                SnackBarMessage(
                    headerMessage = "에러가 발생했어요.",
                    contentMessage = uiState.exception.message.toString()
                )
            )
        }
    }
}

@Composable
internal fun OnSuccessCalendarScreen(
    uiState: CalendarViewModel.UiState.Success,
    calendarData: CalendarData,
    popBackStack: () -> Unit,
    setCalendarData: (CalendarData) -> Unit,
) {
    var popUpState by remember {
        mutableStateOf(PopUpState.getInitValues())
    }

    DefaultLayout(
        modifier = Modifier.addChartPopUpDismiss(
            popUpState = popUpState,
            setPopUpState = { state -> popUpState = state }
        ),
        contentPaddingValues = PaddingValues(vertical = 16.dp, horizontal = 12.dp),
        topBar = {
            CalendarAppBar(
                calendarData = calendarData,
                popBackStack = popBackStack,
                setCalendarData = setCalendarData,
            )
        }
    ) {

        CalendarLayout(
            calendarData = calendarData,
            setCalendarData = setCalendarData
        )

        VerticalSpacer(height = 20.dp)

        CalendarHealthChart(
            graph = uiState.healthTab.graph,
            header = "걸음수",
            type = calendarData.type,
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

        VerticalWeightSpacer(float = 1f)

        CalendarHealthChart(
            graph = uiState.healthTab.graph.map {
                ceil(CaloriesMenuFactory.cal(it.toFloat()).toDouble()).roundToLong()
            },
            header = "칼로리(Kcal)",
            type = calendarData.type,
            barColor = listOf(
                StepWalkColor.orange_700.color,
                StepWalkColor.orange_600.color,
                StepWalkColor.orange_500.color,
                StepWalkColor.orange_400.color,
                StepWalkColor.orange_300.color,
                StepWalkColor.orange_200.color,
            ),
            popUpState = popUpState,
            setPopUpState = { state -> popUpState = state },
        )

        VerticalWeightSpacer(float = 1f)

        CalendarHealthChart(
            graph = uiState.healthTab.graph.map {
                TimeMenuFactory.cal(it).roundToLong()
            },
            header = "걸은 시간(분)",
            type = calendarData.type,
            barColor = listOf(
                StepWalkColor.green_700.color,
                StepWalkColor.green_600.color,
                StepWalkColor.green_500.color,
                StepWalkColor.green_400.color,
                StepWalkColor.green_300.color,
                StepWalkColor.green_200.color,
            ),
            popUpState = popUpState,
            setPopUpState = { state -> popUpState = state },
        )
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = Color.WHITE.toLong())
private fun PreviewCalendarScreen(
    @PreviewParameter(HomeUiStatePreviewParameters::class, limit = 1)
    uiState: HomeUiState,
) = StepWalkTheme {
    CalendarScreen(
        uiState = CalendarViewModel.UiState.Success(uiState.step),
        calendarData = CalendarData.getInitValues(),
        popBackStack = {},
        setCalendarData = {},
        showSnackBar = {},
    )
}