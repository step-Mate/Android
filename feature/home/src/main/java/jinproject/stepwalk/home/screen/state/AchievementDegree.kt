package jinproject.stepwalk.home.screen.state

import androidx.compose.runtime.Stable
import jinproject.stepwalk.design.theme.StepWalkColor

@Stable
internal enum class AchievementDegree {
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

internal fun Float.toAchievementDegree() = when (this) {
    in 0.75f..Float.MAX_VALUE -> AchievementDegree.Perfect
    in 0.5f..0.75f -> AchievementDegree.Normal
    in 0.25f..0.5f -> AchievementDegree.Less
    else -> AchievementDegree.Lack
}