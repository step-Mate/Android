package com.stepmate.home.screen.calendar

import android.graphics.Color
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
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
import com.stepmate.core.SnackBarMessage
import com.stepmate.design.component.StepMateProgressIndicatorRotating
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.design.component.layout.DefaultLayout
import com.stepmate.design.component.layout.chart.PopUpState
import com.stepmate.design.component.layout.chart.addChartPopUpDismiss
import com.stepmate.design.theme.StepWalkColor
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.home.screen.calendar.component.CalendarAppBar
import com.stepmate.home.screen.calendar.component.calendar.CalendarLayout
import com.stepmate.home.screen.calendar.component.chart.CalendarHealthChart
import com.stepmate.home.screen.home.HomeUiState
import com.stepmate.home.screen.home.HomeUiStatePreviewParameters
import com.stepmate.home.screen.home.state.CaloriesMenuFactory
import com.stepmate.home.screen.home.state.TimeMenuFactory
import com.stepmate.home.screen.home.state.User
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
    val user by calendarViewModel.user.collectAsStateWithLifecycle()

    CalendarScreen(
        uiState = uiState,
        user = user,
        calendarData = calendarData,
        popBackStack = popBackStack,
        setCalendarData = calendarViewModel::setCalendarData,
        showSnackBar = showSnackBar,
    )
}

@Composable
internal fun CalendarScreen(
    uiState: CalendarViewModel.UiState,
    user: User,
    calendarData: CalendarData,
    popBackStack: () -> Unit,
    setCalendarData: (CalendarData) -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    when (uiState) {
        CalendarViewModel.UiState.Loading -> {
            StepMateProgressIndicatorRotating()
        }

        is CalendarViewModel.UiState.Success -> {
            OnSuccessCalendarScreen(
                uiState = uiState,
                user = user,
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
    user: User,
    calendarData: CalendarData,
    popBackStack: () -> Unit,
    setCalendarData: (CalendarData) -> Unit,
) {
    var popUpState by remember {
        mutableStateOf(PopUpState.getInitValues())
    }
    val scrollState = rememberScrollState()

    DefaultLayout(
        modifier = Modifier
            .background(MaterialTheme.colorScheme.background)
            .addChartPopUpDismiss(
                popUpState = popUpState,
                setPopUpState = { bool -> popUpState = popUpState.copy(enabled = bool) }
            )
            .verticalScroll(scrollState),
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

        VerticalSpacer(height = 20.dp)

        CalendarHealthChart(
            graph = uiState.healthTab.graph.map {
                ceil(
                    CaloriesMenuFactory(
                        weight = user.weight,
                    ).cal(it.toFloat()).toDouble()
                ).roundToLong()
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

        VerticalSpacer(height = 20.dp)

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
        VerticalSpacer(height = 20.dp)
    }
}

@Composable
@Preview(showBackground = true, backgroundColor = Color.WHITE.toLong())
private fun PreviewCalendarScreen(
    @PreviewParameter(HomeUiStatePreviewParameters::class, limit = 1)
    uiState: HomeUiState,
) = StepMateTheme {
    CalendarScreen(
        uiState = CalendarViewModel.UiState.Success(uiState.step),
        user = User.getInitValues(),
        calendarData = CalendarData.getInitValues(),
        popBackStack = {},
        setCalendarData = {},
        showSnackBar = {},
    )
}