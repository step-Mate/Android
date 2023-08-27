package jinproject.stepwalk.home.utils

import jinproject.stepwalk.home.state.AchievementDegree

fun Float.toAchievementDegree() = when (this) {
    in 0.75f..Float.MAX_VALUE -> AchievementDegree.Perfect
    in 0.5f..0.75f -> AchievementDegree.Normal
    in 0.25f..0.5f -> AchievementDegree.Less
    else -> AchievementDegree.Lack
}