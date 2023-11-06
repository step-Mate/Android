package jinproject.stepwalk.home.screen

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.usecase.GetStepUseCase
import jinproject.stepwalk.home.screen.state.Day
import jinproject.stepwalk.home.screen.state.HealthTab
import jinproject.stepwalk.home.screen.state.HeartRate
import jinproject.stepwalk.home.screen.state.HeartRateTabFactory
import jinproject.stepwalk.home.screen.state.Step
import jinproject.stepwalk.home.screen.state.StepTabFactory
import jinproject.stepwalk.home.screen.state.Time
import jinproject.stepwalk.home.screen.state.User
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.transform
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@Stable
internal data class HomeUiState(
    val step: HealthTab,
    val heartRate: HealthTab,
    val user: User,
    val time: Time

) {
    companion object {
        fun getInitValues(): HomeUiState {
            val time: Time = Day

            return HomeUiState(
                step = StepTabFactory.getInstance(emptyList()).getDefaultValues(time),
                heartRate = HeartRateTabFactory.getInstance(emptyList()).getDefaultValues(time),
                user = User.getInitValues(),
                time = time
            )
        }
    }
}

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val getStepUseCase: GetStepUseCase,
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.getInitValues())

    @OptIn(ExperimentalCoroutinesApi::class)
    val uiState = _uiState.filter {
        (it.step.header.total != -1L) || (it.heartRate.header.total != -1L)
        //&& it.user.uid != 0L
    }.flatMapLatest { state ->
        getStepUseCase().transform { step ->
            emit(step.current - step.last)
        }.map { stepNotAddedOnHealthConnect ->
            state.copy(step = state.step.copy(
                header = state.step.header.copy(total = state.step.header.total + stepNotAddedOnHealthConnect),
                graph = state.step.graph.toMutableList().apply {
                    this[this.lastIndex] = this.last() + stepNotAddedOnHealthConnect
                }
            ))
        }
    }.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        HomeUiState.getInitValues()
    )

    private val _stepThisTime = MutableStateFlow(0)
    val stepThisTime get() = _stepThisTime.asStateFlow()

    init {
        getStepThisTime()
    }

    fun setSteps(steps: List<Step>?) = steps?.let {
        _uiState.update { state ->
            state.copy(
                step = StepTabFactory.getInstance(steps).create(
                    time = state.time,
                    goal = 3000
                )
            )
        }
    }

    fun setHeartRates(heartRates: List<HeartRate>?) = heartRates?.let {
        _uiState.update { state ->
            state.copy(
                heartRate = HeartRateTabFactory.getInstance(heartRates).create(
                    time = state.time,
                    goal = 300
                )
            )
        }
    }

    fun setTime(time: Time) = _uiState.update { state ->
        state.copy(time = time)
    }

    private fun getStepThisTime() = getStepUseCase()
        .onEach { steps ->

        }.launchIn(viewModelScope)
}