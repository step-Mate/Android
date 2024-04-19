package com.stepmate.home.screen.home.state

import androidx.compose.runtime.Stable
import com.stepmate.design.theme.StepWalkColor

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
        Lack -> StepWalkColor.red_800.color
    }
}

internal fun Float.toAchievementDegree() = when {
    this < 0.25f -> AchievementDegree.Lack
    this < 0.5f -> AchievementDegree.Less
    this < 0.75f -> AchievementDegree.Normal
    else -> AchievementDegree.Perfect
}