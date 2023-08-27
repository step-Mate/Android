package jinproject.stepwalk.home.state

import androidx.compose.runtime.Stable

@Stable
data class HealthState(
    val type: Page,
    val figure: Int,
    val max: Int
)
