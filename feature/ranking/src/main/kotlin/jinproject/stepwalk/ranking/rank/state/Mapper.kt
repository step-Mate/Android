package jinproject.stepwalk.ranking.rank.state

import jinproject.stepwalk.domain.model.StepRankBoard
import jinproject.stepwalk.domain.model.UserDetailModel
import jinproject.stepwalk.domain.model.UserStepRank
import jinproject.stepwalk.ranking.detail.User
import jinproject.stepwalk.ranking.rank.Rank
import jinproject.stepwalk.ranking.rank.RankBoard

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
    steps = stepRank.data.map { it.figure.toLong() },
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