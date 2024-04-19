package com.stepmate.data.remote.dto.response.rank

import com.google.gson.annotations.SerializedName
import com.stepmate.domain.model.user.RankModel
import com.stepmate.domain.model.user.StepModel
import com.stepmate.domain.model.user.StepRank
import com.stepmate.domain.model.rank.StepRankBoard
import com.stepmate.domain.model.user.User
import com.stepmate.domain.model.rank.UserStepRank
import java.time.ZonedDateTime

data class MonthRankBoardResponse(
    val ranking: Int,
    val rankChange: Int,
    val level: Int,
    @SerializedName("nickname") val nickName: String,
    val monthStep: Int,
    @SerializedName("title") val designation: String?,
)

fun List<MonthRankBoardResponse>.toStepRankBoard() = StepRankBoard(
    this.map { rankBoardResponse ->
        rankBoardResponse.toUserStepRank()
    }
)

fun MonthRankBoardResponse.toUserStepRank() = UserStepRank(
    user = User(
        name = nickName,
        character = "ic_anim_running_1.json",
        level = level,
        designation = designation ?: "",
    ),
    stepRank = StepRank(
        rank = RankModel(
            rankNumber = ranking,
            dailyIncreasedRank = rankChange
        ),
        data = listOf(
            StepModel(
                startTime = ZonedDateTime.now(),
                endTime = ZonedDateTime.now(),
                figure = monthStep,
            )
        ),
    ),
)