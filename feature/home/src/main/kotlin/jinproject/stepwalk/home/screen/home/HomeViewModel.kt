package jinproject.stepwalk.home.screen.home

import androidx.compose.runtime.Stable
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.usecase.setting.StepGoalUseCase
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.screen.home.state.Day
import jinproject.stepwalk.home.screen.home.state.HealthTab
import jinproject.stepwalk.home.screen.home.state.HeartRate
import jinproject.stepwalk.home.screen.home.state.HeartRateTabFactory
import jinproject.stepwalk.home.screen.home.state.Step
import jinproject.stepwalk.home.screen.home.state.StepTabFactory
import jinproject.stepwalk.home.screen.home.state.Time
import jinproject.stepwalk.home.screen.home.state.User
import jinproject.stepwalk.home.screen.home.state.getStartTime
import jinproject.stepwalk.home.utils.onKorea
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@Stable
internal data class HomeUiState(
    val step: HealthTab,
    val heartRate: HealthTab,
    val user: User,
) {
    companion object {
        fun getInitValues(): HomeUiState {
            val time: Time = Day

            return HomeUiState(
                step = StepTabFactory.getInstance(emptyList()).getDefaultValues(time),
                heartRate = HeartRateTabFactory.getInstance(emptyList()).getDefaultValues(time),
                user = User.getInitValues(),
            )
        }
    }
}

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val healthConnector: HealthConnector,
    private val stepGoalUseCase: StepGoalUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.getInitValues())

    val uiState get() = _uiState.asStateFlow()

    private val _time: MutableStateFlow<Time> = MutableStateFlow(Day)
    val time: StateFlow<Time> get() = _time.asStateFlow()

    private val today get() = Instant.now().onKorea()
    private val endTime
        get() = today
            .withHour(23)
            .withMinute(59)
            .toLocalDateTime()

    val permissionLauncher = healthConnector.requestPermissionsActivityContract()

    private val healthDataTypes = setOf(
        StepsRecord::class,
    )

    val permissions =
        healthDataTypes.map {
            HealthPermission.getReadPermission(it)
        }.toMutableSet().apply {
            addAll(healthDataTypes.map { HealthPermission.getWritePermission(it) })
        }.toSet()

    suspend fun checkPermissions() = healthConnector.checkPermissions(permissions)

    suspend fun setDurationHealthData(duration: Duration) {
        val startTime = today
            .truncatedTo(ChronoUnit.DAYS)
            .toLocalDateTime()

        val steps = healthConnector.readStepsByHours(
            startTime = startTime,
            endTime = endTime,
            duration = duration
        )

        setHealthData(
            steps = steps,
        )
    }

    suspend fun setPeriodHealthData() {
        val startTime = time.value.getStartTime(today)

        val steps = healthConnector.readStepsByPeriods(
            startTime = startTime.toLocalDateTime(),
            endTime = endTime,
            period = time.value.toPeriod()
        )

        setHealthData(
            steps = steps,
        )
    }

    private suspend fun setHealthData(
        steps: List<Step>?,
    ) {
        _uiState.update { state ->
            state.copy(
                step = steps?.let {
                    StepTabFactory.getInstance(steps).create(
                        time = time.value,
                        goal = stepGoalUseCase.getStep().first()
                    )
                } ?: StepTabFactory.getInstance(emptyList()).getDefaultValues(time.value),
            )
        }
    }

    fun setTime(time: Time) = _time.update { _ ->
        time
    }

    fun setSteps(steps: List<Step>?) = steps?.let {
        _uiState.update { state ->
            state.copy(
                step = StepTabFactory.getInstance(steps).create(
                    time = time.value,
                    goal = 3000
                )
            )
        }
    }

    fun setHeartRates(heartRates: List<HeartRate>?) = heartRates?.let {
        _uiState.update { state ->
            state.copy(
                heartRate = HeartRateTabFactory.getInstance(heartRates).create(
                    time = time.value,
                    goal = 300
                )
            )
        }
    }
}