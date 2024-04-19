package com.stepmate.home.screen.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.stepmate.core.catchDataFlow
import com.stepmate.domain.usecase.auth.CheckHasTokenUseCase
import com.stepmate.domain.usecase.setting.StepGoalUseCase
import com.stepmate.domain.usecase.user.GetBodyDataUseCases
import com.stepmate.domain.usecase.user.GetMyInfoUseCases
import com.stepmate.home.HealthConnector
import com.stepmate.home.screen.home.state.Day
import com.stepmate.home.screen.home.state.HealthTab
import com.stepmate.home.screen.home.state.HeartRate
import com.stepmate.home.screen.home.state.HeartRateTabFactory
import com.stepmate.home.screen.home.state.Step
import com.stepmate.home.screen.home.state.StepTabFactory
import com.stepmate.home.screen.home.state.Time
import com.stepmate.home.screen.home.state.User
import com.stepmate.home.screen.home.state.getStartTime
import com.stepmate.home.screen.home.state.toHomeUserState
import com.stepmate.home.utils.onKorea
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject

@Stable
internal data class HomeUiState(
    val step: HealthTab,
    val heartRate: HealthTab,
) {
    companion object {
        fun getInitValues(): HomeUiState {
            val time: Time = Day

            return HomeUiState(
                step = StepTabFactory.getInstance(emptyList(), User.getInitValues()).getDefaultValues(time),
                heartRate = HeartRateTabFactory.getInstance(emptyList()).getDefaultValues(time),
            )
        }
    }
}

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val healthConnector: HealthConnector,
    private val stepGoalUseCase: StepGoalUseCase,
    tokenUseCase: CheckHasTokenUseCase,
    private val getMyInfoUseCase: GetMyInfoUseCases,
    private val getBodyDataUseCases: GetBodyDataUseCases,
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.getInitValues())

    val uiState get() = _uiState.asStateFlow()

    private val _time: MutableStateFlow<Time> = MutableStateFlow(Day)
    val time: StateFlow<Time> get() = _time.asStateFlow()

    private var _user: MutableStateFlow<User> = MutableStateFlow(User.getInitValues())
    val user get() = _user.asStateFlow()

    private val today get() = Instant.now().onKorea()
    private val endTime
        get() = today
            .withHour(23)
            .withMinute(59)
            .toLocalDateTime()

    init {
        tokenUseCase.invoke().onEach { hasToken ->
            if (hasToken)
                getMyInfoUseCase().zip(getBodyDataUseCases()) { user, bodyData ->
                    _user.update {
                        user.toHomeUserState().copy(
                            age = bodyData.age,
                            weight = bodyData.weight,
                            height = bodyData.height
                        )
                    }
                }.catchDataFlow(
                    action = { e -> e },
                    onException = { e -> }
                ).collect()
        }.launchIn(viewModelScope)
    }

    suspend fun checkPermissions() =
        healthConnector.checkPermissions(HealthConnector.healthConnectPermissions)

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
                    StepTabFactory.getInstance(steps, user.value).create(
                        time = time.value,
                        goal = stepGoalUseCase.getStep().first()
                    )
                } ?: StepTabFactory.getInstance(emptyList(), User.getInitValues()).getDefaultValues(time.value),
            )
        }
    }

    fun setTime(time: Time) = _time.update { _ ->
        time
    }

    fun setSteps(steps: List<Step>?) = steps?.let {
        _uiState.update { state ->
            state.copy(
                step = StepTabFactory.getInstance(steps, user.value).create(
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