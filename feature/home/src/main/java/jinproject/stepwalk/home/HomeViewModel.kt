package jinproject.stepwalk.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.usecase.GetStepUseCase
import jinproject.stepwalk.domain.usecase.SetStepUseCase
import jinproject.stepwalk.home.state.HealthState
import jinproject.stepwalk.home.state.HeartRate
import jinproject.stepwalk.home.state.HeartRateMenu
import jinproject.stepwalk.home.state.Page
import jinproject.stepwalk.home.state.PageState
import jinproject.stepwalk.home.state.Step
import jinproject.stepwalk.home.state.StepMenu
import jinproject.stepwalk.home.state.Time
import jinproject.stepwalk.home.state.total
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@Stable
internal data class HomeUiState(
    val step: StepMenu,
    val heartRate: HeartRateMenu,
    val user: User,
    val time: Time

) {
    fun toHealthStateList(stepThisHour: Int) = this.run {
        Page.values().map { page ->
            when (page) {
                Page.Step -> {
                    HealthState(
                        type = PageState(
                            menu = step,
                            title = page.display()
                        ),
                        figure = stepThisHour,
                        max = 1500
                    )
                }

                Page.DrinkWater -> {
                    HealthState(
                        type = PageState(
                            menu = step,
                            title = page.display()
                        ),
                        figure = 2500,
                        max = 2000
                    )
                }

                Page.HeartRate -> {
                    HealthState(
                        type = PageState(
                            menu = heartRate,
                            title = page.display()
                        ),
                        figure = heartRate.heartRates
                            .map { it.avg }
                            .average()
                            .toInt(),
                        max = 200
                    )
                }
            }
        }
    }

    companion object {
        fun getInitValues() = HomeUiState(
            step = StepMenu.getInitValues(),
            heartRate = HeartRateMenu.getInitValues(),
            user = User.getInitValues(),
            time = Time.Day
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
    private val getStepUseCase: GetStepUseCase,
    private val setStepUseCase: SetStepUseCase
) : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.getInitValues())
    val uiState get() = _uiState.asStateFlow()

    private val _selectedStepOnGraph = MutableStateFlow(0L)
    val selectedStepOnGraph get() = _selectedStepOnGraph.asStateFlow()

    private val _stepThisHour = MutableStateFlow(0)
    val stepThisHour get() = _stepThisHour.asStateFlow()

    init {
        getStepThisHour()
    }

    fun setSteps(steps: List<Step>) = _uiState.update { state ->
        state.copy(
            step = StepMenu(steps).apply {
                setGraphItems(state.time)
                setMenuDetails(state.user.kg)
            }
        )
    }

    private fun getStepThisHour() = getStepUseCase
        .invoke()
        .onEach { step ->
            _stepThisHour.update { step }
        }.launchIn(viewModelScope)

    fun setStepThisHour(step: Long) {
        viewModelScope.launch {
            setStepUseCase(step)
        }
    }

    fun setSelectedStepOnGraph(step: Long) = _selectedStepOnGraph.update { step }

    fun setHeartRates(heartRates: List<HeartRate>) = _uiState.update { state ->
        state.copy(
            heartRate = HeartRateMenu(heartRates).apply {
                setGraphItems(state.time)
                setMenuDetails()
            }
        )
    }

    fun setTime(time: Time) = _uiState.update { state ->
        state.copy(time = time)
    }
}