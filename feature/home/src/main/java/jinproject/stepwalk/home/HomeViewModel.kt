package jinproject.stepwalk.home

import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.home.state.HealthState
import jinproject.stepwalk.home.state.Page
import jinproject.stepwalk.home.state.PageState
import jinproject.stepwalk.home.state.Step
import jinproject.stepwalk.home.state.StepMenu
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@Stable
internal data class HomeUiState(
    val step: StepMenu,
    val user: User

) {
    fun toHealthStateList() = this.run {
        Page.values().map { page ->
            when(page) {
                Page.Step -> {
                    HealthState(
                        type = PageState(
                            menu = step,
                            title = page.display()
                        ),
                        figure = step.steps
                            .map { it.distance }
                            .reduce { acc, step -> acc + step }
                            .toInt(),
                        max = 5000
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
                            menu = step,
                            title = page.display()
                        ),
                        figure = 100,
                        max = 200
                    )
                }
            }
        }
    }

    companion object {
        fun getInitValues() = HomeUiState(
            step = StepMenu.getInitValues(),
            user = User.getInitValues()
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
            kg = 0f,
            height = 0f
        )
    }
}

@HiltViewModel
internal class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.getInitValues())
    val uiState get() = _uiState.asStateFlow()

    fun setSteps(steps: List<Step>) = _uiState.update { state ->
        state.copy(step = StepMenu(steps))
    }
}