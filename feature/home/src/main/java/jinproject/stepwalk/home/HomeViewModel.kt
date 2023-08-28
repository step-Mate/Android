package jinproject.stepwalk.home

import androidx.annotation.DrawableRes
import androidx.compose.runtime.Stable
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.METs
import jinproject.stepwalk.home.state.Step
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@Stable
data class HomeUiState(
    val steps: List<Step>,
    val user: User

) {
    companion object {
        fun getInitValues() = HomeUiState(
            steps = listOf(
                Step(
                    distance = 0,
                    start = 0,
                    end = 0,
                    type = METs.Walk
                )
            ),
            user = User.getInitValues()
        )
    }
}

@Stable
data class User(
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

interface HealthMenu {
    @Stable
    val details: MutableMap<String, MenuDetail>
}

@Stable
data class MenuDetail(
    val value: Float,
    @DrawableRes val img: Int,
    val intro: String
)

@HiltViewModel
class HomeViewModel @Inject constructor() : ViewModel() {

    private val _uiState: MutableStateFlow<HomeUiState> =
        MutableStateFlow(HomeUiState.getInitValues())
    val uiState get() = _uiState.asStateFlow()

    fun setSteps(steps: List<Step>) = _uiState.update { state ->
        state.copy(steps = steps)
    }
}