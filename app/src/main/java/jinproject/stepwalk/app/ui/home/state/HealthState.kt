package jinproject.stepwalk.app.ui.home.state

import androidx.compose.runtime.Stable

@Stable
data class HealthState(
    val name: String,
    val figure: Int,
    val max: Int
)
