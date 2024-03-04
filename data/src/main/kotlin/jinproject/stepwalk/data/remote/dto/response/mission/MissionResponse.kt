package jinproject.stepwalk.data.remote.dto.response.mission

import com.google.gson.annotations.SerializedName
import jinproject.stepwalk.domain.model.mission.CalorieMission
import jinproject.stepwalk.domain.model.mission.StepMission

internal data class MissionResponse(
    @SerializedName("title") val designation: String,
    val contents: String,
    val goal: Int,
    val missionType: String,
    val complete: Boolean,
)

internal fun List<MissionResponse>.toMissionComponentList() = this.map { missionResponse ->
    when (missionResponse.missionType) { //TODO achieved 가 없음
        "STEP" -> StepMission(
            designation = missionResponse.designation,
            intro = missionResponse.contents,
            achieved = 0,
            goal = missionResponse.goal,
        )

        "CALORIE" -> CalorieMission(
            designation = missionResponse.designation,
            intro = missionResponse.contents,
            achieved = 0,
            goal = missionResponse.goal,
        )

        else -> throw IllegalArgumentException("알 수 없는 미션 타입: [${missionResponse.missionType}] 입니다.")
    }
}