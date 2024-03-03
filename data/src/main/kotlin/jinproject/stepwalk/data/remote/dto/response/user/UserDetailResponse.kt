package jinproject.stepwalk.data.remote.dto.response.user

import com.google.gson.annotations.SerializedName
import jinproject.stepwalk.data.remote.dto.response.mission.MissionResponse
import jinproject.stepwalk.data.remote.dto.response.mission.toMissionComponentList
import jinproject.stepwalk.domain.model.user.RankModel
import jinproject.stepwalk.domain.model.user.StepRank
import jinproject.stepwalk.domain.model.user.User
import jinproject.stepwalk.domain.model.user.UserDetailModel

internal data class UserDetailResponse(
    @SerializedName("ranking") val rankNumber: Int,
    @SerializedName("rankChange") val dailyIncreasedRank: Int,
    @SerializedName("nickname") val name: String,
    val level: Int,
    val totalStep: Int,
    @SerializedName("title") val designation: String?,
    val dailySteps: List<StepResponse>,
    val missions: List<MissionResponse>,
)

internal fun UserDetailResponse.toUserDetailModel() = UserDetailModel(
    user = User(
        name = name,
        character = "ic_anim_running_1.json",
        level = level,
        designation = designation ?: "",
    ),
    stepRank = StepRank(
        rank = RankModel(
            rankNumber = rankNumber,
            dailyIncreasedRank = dailyIncreasedRank,
        ),
        data = dailySteps.toStepModelList(),
    ),
    mission = missions.toMissionComponentList()
)
