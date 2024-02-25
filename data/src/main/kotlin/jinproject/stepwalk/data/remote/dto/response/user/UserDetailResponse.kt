package jinproject.stepwalk.data.remote.dto.response.user

import com.google.gson.annotations.SerializedName
import jinproject.stepwalk.data.remote.dto.response.mission.MissionResponse
import jinproject.stepwalk.data.remote.dto.response.mission.toMissionComponentList
import jinproject.stepwalk.domain.model.RankModel
import jinproject.stepwalk.domain.model.StepModel
import jinproject.stepwalk.domain.model.StepRank
import jinproject.stepwalk.domain.model.User
import jinproject.stepwalk.domain.model.UserDetailModel
import jinproject.stepwalk.domain.model.mission.CalorieMission
import jinproject.stepwalk.domain.model.mission.StepMission
import java.text.SimpleDateFormat
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.temporal.TemporalAdjusters
import java.util.Locale

internal data class UserDetailResponse(
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
            rankNumber = 0,
            dailyIncreasedRank = 0,
        ),
        data = dailySteps.toStepModelList(),
    ),
    mission = missions.toMissionComponentList()
)
