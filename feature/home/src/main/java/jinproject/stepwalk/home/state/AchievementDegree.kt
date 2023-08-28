package jinproject.stepwalk.home.state

import androidx.compose.runtime.Stable
import jinproject.stepwalk.design.theme.StepWalkColor

@Stable
enum class AchievementDegree {
    Perfect,
    Normal,
    Less,
    Lack;

    fun toColor() = when (this) {
        Perfect -> StepWalkColor.blue_500.color
        Normal -> StepWalkColor.orange_300.color
        Less -> StepWalkColor.yellow_300.color
        Lack -> StepWalkColor.red.color
    }
}