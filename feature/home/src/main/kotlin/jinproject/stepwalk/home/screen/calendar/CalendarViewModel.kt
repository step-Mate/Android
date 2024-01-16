package jinproject.stepwalk.home.screen.calendar

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.screen.calendar.state.ZonedTime
import jinproject.stepwalk.home.screen.calendar.state.ZonedTimeRange
import jinproject.stepwalk.home.screen.home.state.Day
import jinproject.stepwalk.home.screen.home.state.HealthTab
import jinproject.stepwalk.home.screen.home.state.Month
import jinproject.stepwalk.home.screen.home.state.Step
import jinproject.stepwalk.home.screen.home.state.StepTabFactory
import jinproject.stepwalk.home.screen.home.state.Time
import jinproject.stepwalk.home.screen.home.state.Year
import jinproject.stepwalk.home.screen.home.state.getStartTime
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
) : ViewModel() {

    private val _calendarData: MutableStateFlow<CalendarData> =
        MutableStateFlow(CalendarData.getInitValues())
    val calendarData: StateFlow<CalendarData> get() = _calendarData.asStateFlow()

    private val _uiState: MutableStateFlow<UiState> = MutableStateFlow(UiState.Loading)
    val uiState: StateFlow<UiState> get() = _uiState.asStateFlow()

    init {
        viewModelScope.launch(Dispatchers.IO) {
            getTimeList()
        }
    }

    private suspend fun getTimeList() {
        val today = ZonedDateTime.now()
        val steps = healthConnector.readStepsByPeriods(
            startTime = today.withYear(2022).toLocalDateTime(),
            endTime = today.toLocalDateTime(),
            period = Month.toPeriod()
        )

        val startTime = steps?.first { it.distance != 0L }?.startTime ?: today
        val endTime = ZonedTime(today)

        val timeRange = ZonedTimeRange(ZonedTime(startTime), endTime)

        _calendarData.update { calendarData.value.copy(range = timeRange) }
    }

    fun setCalendarData(data: CalendarData) = _calendarData.update { data }

    suspend fun setPeriodSteps() {
        val startTime = calendarData.value.type
            .getStartTime(calendarData.value.selectedTime)
            .toLocalDateTime()

        val endTime = if (ZonedDateTime.now().year == calendarData.value.selectedTime.year)
            ZonedDateTime.now()
        else
            when (calendarData.value.type) {
                Year -> calendarData.value.selectedTime
                    .withMonth(12)
                    .withDayOfMonth(31)

                Month -> calendarData.value.selectedTime
                    .with(TemporalAdjusters.lastDayOfMonth())

                else -> throw IllegalArgumentException("년 또는 달 간의 데이터만 출력할 수 있음")
            }

        val steps = healthConnector.readStepsByPeriods(
            startTime = startTime,
            endTime = endTime.toLocalDateTime(),
            period = calendarData.value.type.toPeriod()
        )

        setHealthData(
            steps = steps,
        )
    }

    suspend fun setDaySteps() {
        val startTime = calendarData.value.selectedTime
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
        )
    }

    private fun setHealthData(
        steps: List<Step>?,
    ) {
        getHealthTab {
            steps?.let {
                StepTabFactory.getInstance(steps).create(
                    time = calendarData.value.type,
                    goal = 3000
                )
            } ?: StepTabFactory.getInstance(emptyList()).getDefaultValues(calendarData.value.type)
        }
    }

    private fun getHealthTab(block: () -> HealthTab) {

        _uiState.update {
            UiState.Loading
        }

        _uiState.update {
            kotlin.runCatching {
                UiState.Success(block())
            }.getOrElse { t ->
                UiState.Error(t)
            }
        }

    }

    @Stable
    sealed class UiState {
        data object Loading : UiState()
        data class Success(val healthTab: HealthTab) : UiState()
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