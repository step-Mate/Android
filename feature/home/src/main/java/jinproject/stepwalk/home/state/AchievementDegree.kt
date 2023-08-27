package jinproject.stepwalk.home.state

import androidx.compose.runtime.Stable
import jinproject.stepwalk.design.theme.MiscellaneousToolColor

@Stable
enum class AchievementDegree {
    Perfect,
    Normal,
    Less,
    Lack;

    fun toColor() = when (this) {
        Perfect -> MiscellaneousToolColor.blue.color
        Normal -> MiscellaneousToolColor.orange_300.color
        Less -> MiscellaneousToolColor.yellow_300.color
        Lack -> MiscellaneousToolColor.red.color
    }
}