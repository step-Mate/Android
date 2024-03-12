package com.stepmate.domain.model.rank

import com.stepmate.domain.model.user.StepRank
import com.stepmate.domain.model.user.User

class StepRankBoard(
    val list: List<UserStepRank>
) {
    companion object {
        fun createRankBoardModel(list: List<UserStepRank>) = StepRankBoard(list.sorted())
    }
}

data class UserStepRank(
    val user: User,
    val stepRank: StepRank,
): Comparable<UserStepRank> {
    override fun compareTo(other: UserStepRank): Int =
        when {
            stepRank.rank.rankNumber > other.stepRank.rank.rankNumber -> 1
            stepRank.rank.rankNumber < other.stepRank.rank.rankNumber -> -1
            else -> {
                user.level.compareTo(other.user.level).let { c ->
                    if (c == 0)
                        user.name.compareTo(other.user.name)
                    else
                        c
                }
            }
        }
}