package com.stepmate.data.local.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.stepmate.domain.model.mission.CalorieMission
import com.stepmate.domain.model.mission.CalorieMissionLeaf
import com.stepmate.domain.model.mission.MissionCommon
import com.stepmate.domain.model.mission.MissionComposite
import com.stepmate.domain.model.mission.StepMission
import com.stepmate.domain.model.mission.StepMissionLeaf

data class LocalMissionList(
    @Embedded val mission: Mission,
    @Relation(
        parentColumn = "designation",
        entityColumn = "designation"
    )
    val leaf: List<MissionLeaf>
)

internal fun List<LocalMissionList>.toMissionDataList(): Map<String, List<MissionCommon>> {
    val missionList = HashMap<String, ArrayList<MissionCommon>>()
    this.forEach { localMission ->
        if (localMission.leaf.size == 1) {
            when (localMission.leaf.first().type) {
                MissionType.Step -> {
                    val missions = missionList.getOrDefault(
                        localMission.mission.title,
                        arrayListOf()
                    )
                    missions.add(
                        StepMission(
                            designation = localMission.mission.designation,
                            intro = localMission.mission.intro,
                            achieved = localMission.leaf.first().achieved,
                            goal = localMission.leaf.first().goal
                        )
                    )
                    missionList[localMission.mission.title] = missions
                }

                MissionType.Calorie -> {
                    val missions = missionList.getOrDefault(
                        localMission.mission.title,
                        arrayListOf()
                    )
                    missions.add(
                        CalorieMission(
                            designation = localMission.mission.designation,
                            intro = localMission.mission.intro,
                            achieved = localMission.leaf.first().achieved,
                            goal = localMission.leaf.first().goal
                        )
                    )
                    missionList[localMission.mission.title] = missions
                }

                else -> {}
            }
        } else {
            val leafList = ArrayList<com.stepmate.domain.model.mission.MissionLeaf>()
            localMission.leaf.forEach { leaf ->
                when (leaf.type) {
                    MissionType.Step -> {
                        leafList.add(
                            StepMissionLeaf(
                                achieved = leaf.achieved,
                                goal = leaf.goal
                            )
                        )
                    }

                    MissionType.Calorie -> {
                        leafList.add(
                            CalorieMissionLeaf(
                                achieved = leaf.achieved,
                                goal = leaf.goal
                            )
                        )
                    }

                    else -> {}
                }
            }
            val missions = missionList.getOrDefault(
                localMission.mission.title,
                arrayListOf()
            )
            missions.add(
                MissionComposite(
                    designation = localMission.mission.designation,
                    intro = localMission.mission.intro,
                    missions = leafList
                )
            )
            missionList[localMission.mission.title] = missions
        }
    }
    return missionList.mapValues { it.value.toList() }
}

fun Map<String, List<MissionCommon>>.toLocalMissionList(): Pair<List<Mission>, List<MissionLeaf>> {
    val missionPair = Pair<ArrayList<Mission>, ArrayList<MissionLeaf>>(
        arrayListOf(),
        arrayListOf()
    )
    this.forEach { missionList ->
        missionList.value.forEach { mission ->
            missionPair.first.add(
                Mission(
                    title = missionList.key,
                    designation = mission.designation,
                    intro = mission.intro
                )
            )
            if (mission is MissionComposite) {
                mission.missions.forEach { leaf ->
                    missionPair.second.add(
                        MissionLeaf(
                            id = 0,
                            designation = mission.designation,
                            type = leaf.getType(),
                            achieved = leaf.getMissionAchieved(),
                            goal = leaf.getMissionGoal()
                        )
                    )
                }
            } else {
                missionPair.second.add(
                    MissionLeaf(
                        id = 0,
                        designation = mission.designation,
                        type = mission.getType(),
                        achieved = mission.getMissionAchieved(),
                        goal = mission.getMissionGoal()
                    )
                )
            }
        }
    }
    return missionPair
}
