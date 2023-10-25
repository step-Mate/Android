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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
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
        fun getInitValues() = HomeUiState(
            step = HealthTab.getInitValues(Day.toNumberOfDays()),
            heartRate = HealthTab.getInitValues(Day.toNumberOfDays()),
            user = User.getInitValues(),
            time = Day
        )
    }
}

@Stable
internal data class User(
    val uid: Long,
    val name: String,
    val age: Int,
    val kg: Float,
    val height: Float,
) {
    companion object {
        fun getInitValues() = User(
            uid = 0L,
            name = "",
            age = 0,
            kg = 55f,
            height = 0f
        )
    }
}

@HiltViewModel
internal class HomeViewModel @Inject constructor(
    private val getStepUseCase: GetStepUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.getInitValues())
    val uiState get() = _uiState.asStateFlow()

    private val _stepThisHour = MutableStateFlow(0)
    val stepThisHour get() = _stepThisHour.asStateFlow()

    init {
        getStepThisHour()
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

    private fun getStepThisHour() = getStepUseCase()
        .onEach { steps ->
            _stepThisHour.update { steps.first().toInt() }
        }.launchIn(viewModelScope)

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
}