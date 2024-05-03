package com.stepmate.data.local.datasource.impl

import com.stepmate.data.local.database.dao.MissionLocal
import com.stepmate.data.local.database.entity.Mission
import com.stepmate.data.local.database.entity.MissionLeaf
import com.stepmate.data.local.database.entity.toMissionDataList
import com.stepmate.data.local.datasource.LocalMissionDataSource
import com.stepmate.domain.model.mission.CalorieMission
import com.stepmate.domain.model.mission.CalorieMissionLeaf
import com.stepmate.domain.model.mission.MissionCommon
import com.stepmate.domain.model.mission.MissionComposite
import com.stepmate.domain.model.mission.MissionFigure
import com.stepmate.domain.model.mission.MissionList
import com.stepmate.domain.model.mission.MissionType
import com.stepmate.domain.model.mission.StepMission
import com.stepmate.domain.model.mission.StepMissionLeaf
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LocalMissionDataSourceImpl @Inject constructor(
    private val missionLocal: MissionLocal,
) : LocalMissionDataSource {
    override fun getAllMissionList(): Flow<List<MissionList>> =
        missionLocal.getAllMissionList().map { it.toMissionDataList() }

    override fun getMissionList(title: String): Flow<MissionList> =
        missionLocal.getMissionList(title).map { list ->
            val missionList = ArrayList<MissionCommon>()
            list.forEach { missions ->
                if (missions.leaf.size == 1) {
                    when (missions.leaf.first().type) {
                        MissionType.Step -> {
                            missionList.add(
                                StepMission(
                                    designation = missions.mission.designation,
                                    intro = missions.mission.intro,
                                    achieved = missions.leaf.first().achieved,
                                    goal = missions.leaf.first().goal
                                )
                            )
                        }

                        MissionType.Calorie -> {
                            missionList.add(
                                CalorieMission(
                                    designation = missions.mission.designation,
                                    intro = missions.mission.intro,
                                    achieved = missions.leaf.first().achieved,
                                    goal = missions.leaf.first().goal
                                )
                            )
                        }
                    }
                } else {
                    val leafList = ArrayList<MissionFigure>()
                    missions.leaf.forEach { leaf ->
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
                        }
                    }
                    missionList.add(
                        MissionComposite(
                            designation = missions.mission.designation,
                            intro = missions.mission.intro,
                            missions = leafList
                        )
                    )
                }
            }
            MissionList(title, missionList)
        }

    override suspend fun updateMissionList(apiList: List<MissionList>) =
        withContext(Dispatchers.IO) {
            val originalList = async {
                missionLocal.getAllMissionList().first().toMissionDataList().sortedBy { it.title }
            }
            if (originalList.await().isEmpty() || apiList != originalList) {
                apiList.forEach { missionList ->
                    missionList.list.forEach { detail ->
                        missionLocal.addMission(
                            Mission(
                                title = missionList.title,
                                designation = detail.designation,
                                intro = detail.intro
                            )
                        )
                        val mission = originalList.await()
                            .find { it.title == missionList.title }?.list?.find { it.designation == detail.designation }
                        if (detail is MissionComposite) {
                            detail.missions.forEach { leaf ->
                                val type = when (leaf) {
                                    is StepMissionLeaf -> MissionType.Step
                                    is CalorieMissionLeaf -> MissionType.Calorie
                                    else -> MissionType.Step
                                }
                                val localAchieved = mission?.let { missionType ->
                                    when (type) {
                                        MissionType.Step -> (missionType as MissionComposite).missions.find { it is StepMissionLeaf }
                                            ?.getMissionAchieved() ?: 0

                                        MissionType.Calorie -> (missionType as MissionComposite).missions.find { it is CalorieMissionLeaf }
                                            ?.getMissionAchieved() ?: 0
                                    }
                                } ?: 0
                                missionLocal.addMissionLeaf(
                                    MissionLeaf(
                                        id = 0,
                                        designation = detail.designation,
                                        type = type,
                                        achieved = when {
                                            localAchieved >= leaf.getMissionAchieved() -> localAchieved
                                            else -> leaf.getMissionAchieved()
                                        },
                                        goal = leaf.getMissionGoal()
                                    )
                                )
                            }
                        } else {
                            val type = when (detail) {
                                is StepMission -> MissionType.Step
                                is CalorieMission -> MissionType.Calorie
                                else -> throw IllegalArgumentException("알 수 없는 미션 타입입니다.")
                            }
                            val localAchieved = mission?.getMissionAchieved() ?: 0
                            missionLocal.addMissionLeaf(
                                MissionLeaf(
                                    id = 0,
                                    designation = detail.designation,
                                    type = type,
                                    achieved = when {
                                        localAchieved >= detail.getMissionAchieved() -> localAchieved
                                        else -> detail.getMissionAchieved()
                                    },
                                    goal = detail.getMissionGoal()
                                )
                            )
                        }
                    }
                }
            }
        }

    override suspend fun updateMission(step: Int, calories: Int) = withContext(Dispatchers.IO) {
        val timeStep = getMissionTimeAchieved(MissionType.Step).first() + step
        val timeCalories = getMissionTimeAchieved(MissionType.Calorie).first() + calories
        val localStep = getMissionAchieved(MissionType.Step).first() + step
        val localCalories = getMissionAchieved(MissionType.Calorie).first() + calories
        missionLocal.updateMissionAchieved(MissionType.Step, localStep)
        missionLocal.updateMissionAchieved(MissionType.Calorie, localCalories)
        missionLocal.updateMissionTimeAchieved(MissionType.Step, timeStep)
        missionLocal.updateMissionTimeAchieved(
            MissionType.Calorie,
            timeCalories
        )
    }

    override suspend fun resetMissionTime() =
        missionLocal.resetMissionTime()

    private fun getMissionAchieved(missionType: MissionType): Flow<Int> =
        missionLocal.getMissionAchieved(missionType)

    private fun getMissionTimeAchieved(missionType: MissionType): Flow<Int> =
        missionLocal.getMissionTimeAchieved(missionType)


}