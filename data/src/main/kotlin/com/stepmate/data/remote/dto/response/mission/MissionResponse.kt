package com.stepmate.data.remote.dto.response.mission

import com.google.gson.annotations.SerializedName
import com.stepmate.domain.model.mission.CalorieMission
import com.stepmate.domain.model.mission.StepMission
import com.stepmate.domain.model.mission.CalorieMissionLeaf
import com.stepmate.domain.model.mission.MissionCommon
import com.stepmate.domain.model.mission.MissionComposite
import com.stepmate.domain.model.mission.MissionFigure
import com.stepmate.domain.model.mission.MissionList
import com.stepmate.domain.model.mission.StepMissionLeaf

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

data class MissionsResponse(
    val title : String,
    val designation: String,
    val contents: String,
    val detail : List<MissionDetailResponse>
)

data class MissionDetailResponse(
    val missionType: String,
    val currentValue : Float,
    val goal: Int
)

internal fun List<MissionsResponse>.toMissionList() : List<MissionList> {
    val missionList = HashMap<String, ArrayList<MissionCommon>>()
    this.forEach {missionResponse ->
        if (missionResponse.detail.size == 1){
            missionResponse.detail.forEach { detail ->
                when(detail.missionType){
                    "STEP" -> {
                        val missions = missionList.getOrDefault(
                            missionResponse.title,
                            arrayListOf()
                        )
                        missions.add(
                            StepMission(
                                designation = missionResponse.designation,
                                intro = missionResponse.contents,
                                achieved = detail.currentValue.toInt(),
                                goal = detail.goal
                            )
                        )
                        missionList[missionResponse.title] = missions
                    }
                    "CALORIE" -> {
                        val missions = missionList.getOrDefault(
                            missionResponse.title,
                            arrayListOf()
                        )
                        missions.add(
                            CalorieMission(
                                designation = missionResponse.designation,
                                intro = missionResponse.contents,
                                achieved = detail.currentValue.toInt(),
                                goal = detail.goal,
                            )
                        )
                        missionList[missionResponse.title] = missions
                    }
                    else -> throw IllegalArgumentException("알 수 없는 미션 타입: [${detail.missionType}] 입니다.")
                }
            }
        }else{
            val leafList = ArrayList<MissionFigure>()
            missionResponse.detail.forEach { detail ->
                when(detail.missionType){
                    "STEP" -> {
                        leafList.add(
                            StepMissionLeaf(
                                achieved = detail.currentValue.toInt(),
                                goal = detail.goal
                            )
                        )
                    }
                    "CALORIE" -> {
                        leafList.add(
                            CalorieMissionLeaf(
                                achieved = detail.currentValue.toInt(),
                                goal = detail.goal,
                            )
                        )
                    }
                    else -> throw IllegalArgumentException("알 수 없는 미션 타입: [${detail.missionType}] 입니다.")
                }
            }
            val missions = missionList.getOrDefault(
                missionResponse.title,
                arrayListOf()
            )
            missions.add(
                MissionComposite(
                    designation = missionResponse.designation,
                    intro = missionResponse.contents,
                    missions = leafList
                )
            )
            missionList[missionResponse.title] = missions
        }
    }
    return missionList.map {
        MissionList(it.key,it.value)
    }
}