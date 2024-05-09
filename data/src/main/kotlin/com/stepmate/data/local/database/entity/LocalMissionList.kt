package com.stepmate.data.local.database.entity

import androidx.room.Embedded
import androidx.room.Relation
import com.stepmate.domain.model.mission.CalorieMission
import com.stepmate.domain.model.mission.CalorieMissionLeaf
import com.stepmate.domain.model.mission.MissionCommon
import com.stepmate.domain.model.mission.MissionComposite
import com.stepmate.domain.model.mission.MissionList
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

internal fun List<LocalMissionList>.toMissionDataList(): List<MissionList> {
    val missionList = HashMap<String, ArrayList<MissionCommon>>()
    this.forEach {
        if (it.leaf.size == 1) {
            when (it.leaf.first().type) {
                MissionType.Step -> {
                    val missions = missionList.getOrDefault(
                        it.mission.title,
                        arrayListOf()
                    )
                    missions.add(
                        StepMission(
                            designation = it.mission.designation,
                            intro = it.mission.intro,
                            achieved = it.leaf.first().achieved,
                            goal = it.leaf.first().goal
                        )
                    )
                    missionList[it.mission.title] = missions
                }

                MissionType.Calorie -> {
                    val missions = missionList.getOrDefault(
                        it.mission.title,
                        arrayListOf()
                    )
                    missions.add(
                        CalorieMission(
                            designation = it.mission.designation,
                            intro = it.mission.intro,
                            achieved = it.leaf.first().achieved,
                            goal = it.leaf.first().goal
                        )
                    )
                    missionList[it.mission.title] = missions
                }
                else -> {}
            }
        } else {
            val leafList = ArrayList<com.stepmate.domain.model.mission.MissionLeaf>()
            it.leaf.forEach { leaf ->
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
                it.mission.title,
                arrayListOf()
            )
            missions.add(
                MissionComposite(
                    designation = it.mission.designation,
                    intro = it.mission.intro,
                    missions = leafList
                )
            )
            missionList[it.mission.title] = missions
        }
    }
    return missionList.map {
        MissionList(it.key, it.value)
    }
}

fun List<MissionList>.toLocalMissionList() : Pair<List<Mission>,List<MissionLeaf>> {
    val missionPair = Pair<ArrayList<Mission>, ArrayList<MissionLeaf>>(
        arrayListOf(),
        arrayListOf()
    )
    this.forEach { missionList ->
        missionList.list.forEach { mission ->
            missionPair.first.add(
                Mission(
                    title = missionList.title,
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
