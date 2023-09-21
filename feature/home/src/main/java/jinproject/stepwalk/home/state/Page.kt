package jinproject.stepwalk.home.state

import androidx.compose.runtime.Stable

@Stable
internal data class PageState(
    val menu: HealthMenu,
    val title: String
)

@Stable
internal enum class Page {
    Step,
    HeartRate,
    DrinkWater;

    fun display() = when(this) {
        Step -> "걸음수"
        HeartRate -> "심박수"
        DrinkWater -> "물 섭취량"
    }
}