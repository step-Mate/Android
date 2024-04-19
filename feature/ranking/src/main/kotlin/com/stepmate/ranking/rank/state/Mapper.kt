package com.stepmate.ranking.rank.state

import com.stepmate.domain.model.rank.StepRankBoard
import com.stepmate.domain.model.rank.UserStepRank
import com.stepmate.domain.model.user.UserDetailModel
import com.stepmate.ranking.detail.User
import com.stepmate.ranking.rank.Rank
import com.stepmate.ranking.rank.RankBoard

internal fun UserDetailModel.asUser(maxStep: Int) = User(
    info = Rank(
        name = user.name,
        character = user.character,
        level = user.level,
        step = stepRank.getTotalHealthFigure(),
        designation = user.designation,
        rankNumber = stepRank.rank.rankNumber,
        dailyIncreasedRank = stepRank.rank.dailyIncreasedRank,
    ),
    maxStep = maxStep,
    steps = stepRank.data.map { stepModel -> stepModel.figure.toLong() },
    latestMissions = mission,
)

internal fun StepRankBoard.asRankBoard(page: Int) = RankBoard(
    rankList = list.map { userRankModel -> userRankModel.asRank() },
    page = page
)

internal fun UserStepRank.asRank() = Rank(
    name = user.name,
    character = user.character,
    level = user.level,
    step = stepRank.getTotalHealthFigure(),
    designation = user.designation,
    rankNumber = stepRank.rank.rankNumber,
    dailyIncreasedRank = stepRank.rank.dailyIncreasedRank
)