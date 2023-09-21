package jinproject.stepwalk.home.state

import androidx.compose.runtime.Stable

@Stable
internal data class HealthState(
    val type: PageState,
    val figure: Int,
    val max: Int
)
