package jinproject.stepwalk.data.remote.dto.response.rank

import com.google.gson.annotations.SerializedName
import jinproject.stepwalk.domain.model.RankModel
import jinproject.stepwalk.domain.model.StepModel
import jinproject.stepwalk.domain.model.StepRank
import jinproject.stepwalk.domain.model.StepRankBoard
import jinproject.stepwalk.domain.model.User
import jinproject.stepwalk.domain.model.UserStepRank
import java.time.ZonedDateTime

data class FriendRankBoardResponse(
    val ranking: Int,
    val level: Int,
    @SerializedName("nickname") val nickName: String,
    val monthStep: Int,
    @SerializedName("title")  val designation: String?,
)

fun List<FriendRankBoardResponse>.toStepRankBoard() = StepRankBoard(
    this.map { rankBoardResponse ->
        rankBoardResponse.toUserStepRank()
    }
)

fun FriendRankBoardResponse.toUserStepRank() = UserStepRank(
    user = User(
        name = nickName,
        character = "ic_anim_running_1.json",
        level = level,
        designation = designation ?: "",
    ),
    stepRank = StepRank(
        rank = RankModel(
            rankNumber = ranking,
            dailyIncreasedRank = 0,
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