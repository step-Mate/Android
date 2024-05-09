package com.stepmate.data.repositoryImpl

import com.stepmate.data.local.database.entity.Mission
import com.stepmate.data.local.database.entity.MissionLeaf
import com.stepmate.data.local.database.entity.MissionType
import com.stepmate.data.local.database.entity.getType
import com.stepmate.data.local.database.entity.toLocalMissionList
import com.stepmate.data.local.datasource.BodyDataSource
import com.stepmate.data.local.datasource.LocalMissionDataSource
import com.stepmate.data.remote.dataSource.RemoteMissionDataSource
import com.stepmate.data.remote.utils.stepMateDataFlow
import com.stepmate.domain.model.DesignationState
import com.stepmate.domain.model.mission.MissionComposite
import com.stepmate.domain.model.mission.MissionList
import com.stepmate.domain.repository.MissionRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class MissionRepositoryImpl @Inject constructor(
    private val remoteMissionDataSource: RemoteMissionDataSource,
    private val localMissionDataSource: LocalMissionDataSource,
    private val bodyDataSource: BodyDataSource
) : MissionRepository {

    override suspend fun selectDesignation(designation: String) =
        remoteMissionDataSource.selectDesignation(designation)

    override fun getDesignation(): Flow<DesignationState> = stepMateDataFlow {
        remoteMissionDataSource.getDesignation()
    }

    override suspend fun checkCompleteMission(): List<String> = withContext(Dispatchers.IO) {
        val localDesignationList = async(Dispatchers.Default) {
            localMissionDataSource.getAllMissionList().first().map { missions ->
                val complete = arrayListOf<String>()
                missions.list.forEach { missionCommon ->
                    if (missionCommon.getMissionProgress() == 1f)
                        complete.add(missionCommon.designation)
                    else
                        return@forEach
                }
                complete
            }.flatten().sorted()
        }
        val designationList = remoteMissionDataSource.getDesignation().list.sorted()
        val complete = localDesignationList.await().subtract(designationList.toSet()).toList()
        complete.forEach { designation ->
            remoteMissionDataSource.completeMission(designation)
        }
        complete
    }

    override fun getAllLocalMissionList(): Flow<List<MissionList>> =
        localMissionDataSource.getAllMissionList().map { list ->
            list.map { mission ->
                MissionList(mission.title,
                    mission.list.sortedBy { it.getMissionGoal() }
                )
            }
        }

    override fun getLocalMissionList(title: String): Flow<MissionList> =
        localMissionDataSource.getMissionList(title).map { mission ->
            MissionList(mission.title,
                mission.list.sortedBy { it.getMissionGoal() }
            )
        }

    override suspend fun synchronizationMissionList() = withContext(Dispatchers.IO) {
        val localMissions = async { localMissionDataSource.getAllMissionList().first() }
        val remoteMissions = remoteMissionDataSource.getMissionList()
        when {
            localMissions.await()
                .isEmpty() || remoteMissions.sumOf { it.list.size } != localMissions.await()
                .sumOf { it.list.size } -> {
                val missionPair: Pair<List<Mission>, List<MissionLeaf>> =
                    remoteMissions.toLocalMissionList()
                localMissionDataSource.addMissions(mission = missionPair.first.toTypedArray())
                localMissionDataSource.addMissionLeafs(missionLeaf = missionPair.second.toTypedArray())
            }

            remoteMissions != localMissions -> {//기존 미션 업데이트
                remoteMissions.forEach { missionList ->
                    missionList.list.forEach { mission ->
                        if (mission is MissionComposite) {
                            mission.missions.forEach { leaf ->
                                localMissionDataSource.synchronizationMission(
                                    designation = mission.designation,
                                    type = leaf.getType(),
                                    achieved = leaf.achieved
                                )
                            }
                        } else {
                            localMissionDataSource.synchronizationMission(
                                designation = mission.designation,
                                type = mission.getType(),
                                achieved = mission.getMissionAchieved()
                            )
                        }
                    }
                }
            }
        }
    }


    override suspend fun updateStepAndCalories(step: Int) {
        localMissionDataSource.updateMission(
            type = MissionType.Step,
            achieved = step,
        )
        localMissionDataSource.updateMission(
            type = MissionType.Calorie,
            achieved = bodyDataSource.getCalories(step).toInt()
        )
    }

    override suspend fun resetMissionTime() =
        localMissionDataSource.resetMissionTime()
}