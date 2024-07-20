package com.stepmate.home.screen.calendar

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepmate.domain.usecase.user.GetBodyDataUseCase
import com.stepmate.home.HealthConnector
import com.stepmate.home.screen.calendar.state.ZonedTime
import com.stepmate.home.screen.calendar.state.ZonedTimeRange
import com.stepmate.home.screen.home.state.Day
import com.stepmate.home.screen.home.state.HealthTab
import com.stepmate.home.screen.home.state.Month
import com.stepmate.home.screen.home.state.Step
import com.stepmate.home.screen.home.state.Time
import com.stepmate.home.screen.home.state.Year
import com.stepmate.home.screen.home.state.getGraph
import com.stepmate.home.screen.home.state.getStartTime
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.collections.immutable.PersistentList
import kotlinx.collections.immutable.toPersistentList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.Duration
import java.time.ZonedDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
internal class CalendarViewModel @Inject constructor(
    private val healthConnector: HealthConnector,
    private val getBodyDataUseCase: GetBodyDataUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> get() = _uiState.asStateFlow()

    var userWeight: Int = 0
        private set

    init {
        viewModelScope.launch(Dispatchers.IO) {
            launch {
                initAvailableTimeRange()
            }
        }
    }

    private suspend fun initAvailableTimeRange() {
        val today = ZonedDateTime.now()
        val permittedSteps = healthConnector.readStepsByPeriods(
            startTime = today.withYear(2022).toLocalDateTime(),
            endTime = today.toLocalDateTime(),
            period = Month.toPeriod()
        )

        val startTime = permittedSteps?.firstOrNull { it.distance != 0L }?.startTime ?: today
        val endTime = ZonedTime(today)

        val timeRange = ZonedTimeRange(ZonedTime(startTime), endTime)

        userWeight = getBodyDataUseCase().first().weight

        setDaySteps(CalendarData.getInitValues().copy(range = timeRange))
    }

    fun setCalendarData(data: CalendarData) {
        viewModelScope.launch {
            when (data.type) {
                Day -> {
                    setDaySteps(calendarData = data)
                }

                else -> {
                    setPeriodSteps(calendarData = data)
                }
            }
        }
    }

    private suspend fun setPeriodSteps(calendarData: CalendarData) {
        val startTime = calendarData.type
            .getStartTime(calendarData.selectedTime)
            .toLocalDateTime()

        val endTime =
            if (ZonedDateTime.now().year == calendarData.selectedTime.year && ZonedDateTime.now().monthValue == calendarData.selectedTime.dayOfMonth)
                ZonedDateTime.now()
            else
                when (calendarData.type) {
                    Year -> calendarData.selectedTime
                        .withMonth(12)
                        .withDayOfMonth(31)

                    Month -> calendarData.selectedTime
                        .with(TemporalAdjusters.lastDayOfMonth())

                    else -> throw IllegalArgumentException("년 또는 달 간의 데이터만 출력할 수 있음")
                }

        val steps = healthConnector.readStepsByPeriods(
            startTime = startTime,
            endTime = endTime.toLocalDateTime(),
            period = calendarData.type.toPeriod()
        )

        setHealthData(
            steps = steps,
            calendarData = calendarData,
        )
    }

    private suspend fun setDaySteps(calendarData: CalendarData) {
        val startTime = calendarData.selectedTime
            .truncatedTo(ChronoUnit.DAYS)
        val endTime = startTime
            .withHour(23)
            .withMinute(59)
            .withSecond(59)

        val steps = healthConnector.readStepsByHours(
            startTime = startTime.toLocalDateTime(),
            endTime = endTime.toLocalDateTime(),
            duration = Duration.ofHours(1L)
        )

        setHealthData(
            steps = steps,
            calendarData = calendarData,
        )
    }

    private fun setHealthData(
        steps: List<Step>?,
        calendarData: CalendarData,
    ) {
        createUiState(calendarData = calendarData) {
            val time = calendarData.type

            steps?.let {
                calendarData.type.getGraph(steps).toPersistentList()
            } ?: HealthTab.getDefaultGraphItems(time.toNumberOfDays()).toPersistentList()
        }
    }

    private fun createUiState(calendarData: CalendarData, block: () -> PersistentList<Long>) {

        _uiState.update {
            UiState.Loading
        }

        _uiState.update {
            kotlin.runCatching {
                UiState.Success(steps = block(), calendarData = calendarData)
            }.getOrElse { t ->
                UiState.Error(t)
            }
        }

    }

    @Stable
    sealed class UiState {
        data object Loading : UiState()
        data class Success(
            val steps: PersistentList<Long>,
            val calendarData: CalendarData,
        ) : UiState()

        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }
}

@Stable
internal data class CalendarData(
    val type: Time,
    val range: ZonedTimeRange,
    val selectedTime: ZonedDateTime,
) {
    fun select(time: ZonedDateTime, onSelected: (ZonedDateTime) -> Unit) {
        if (ZonedTime(time) in range)
            onSelected(time)
    }

    companion object {
        fun getInitValues() = CalendarData(
            type = Day,
            selectedTime = ZonedDateTime.now(),
            range = ZonedTime(ZonedDateTime.now())..ZonedTime(ZonedDateTime.now())
        )
    }
}