package com.stepmate.data.repositoryImpl

import com.stepmate.data.local.database.entity.Mission
import com.stepmate.data.local.database.entity.MissionLeaf
import com.stepmate.data.local.database.entity.MissionType
import com.stepmate.data.local.database.entity.toLocalMissionList
import com.stepmate.data.local.datasource.BodyDataSource
import com.stepmate.data.local.datasource.LocalMissionDataSource
import com.stepmate.data.remote.dataSource.RemoteMissionDataSource
import com.stepmate.data.remote.utils.stepMateDataFlow
import com.stepmate.domain.model.mission.MissionCommon
import com.stepmate.domain.repository.MissionRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import javax.inject.Inject

internal class MissionRepositoryImpl @Inject constructor(
    private val remoteMissionDataSource: RemoteMissionDataSource,
    private val localMissionDataSource: LocalMissionDataSource,
    private val bodyDataSource: BodyDataSource
) : MissionRepository {

    override suspend fun selectDesignation(designation: String) =
        remoteMissionDataSource.selectDesignation(designation)

    override fun getDesignation(): Flow<List<String>> = stepMateDataFlow {
        remoteMissionDataSource.getDesignation()
    }

    override suspend fun checkCompleteMission(): List<String> {
        val localDesignationList = localMissionDataSource.getDesignationList()
        val designationList = remoteMissionDataSource.getDesignation()
        val complete = localDesignationList.subtract(designationList.toSet()).toList()
        return complete.onEach { designation ->
            remoteMissionDataSource.completeMission(designation)
        }
    }

    override fun getAllLocalMissionList(): Flow<Map<String, List<MissionCommon>>> =
        localMissionDataSource.getAllMissionList()

    override fun getLocalMissionList(title: String): Flow<List<MissionCommon>> =
        localMissionDataSource.getMissionList(title)

    override suspend fun synchronizationMissionList() = coroutineScope {
        val localMissions = async { localMissionDataSource.getAllMissionList().first() }
        val remoteMissions = remoteMissionDataSource.getMissionList()
        when {
            localMissions.await()
                .isEmpty() || remoteMissions.flatMap { it.value }.size != localMissions.await()
                .flatMap { it.value }.size -> {
                val missionPair: Pair<List<Mission>, List<MissionLeaf>> =
                    remoteMissions.toLocalMissionList()
                localMissionDataSource.addMissions(mission = missionPair.first.toTypedArray())
                localMissionDataSource.addMissionLeafs(missionLeaf = missionPair.second.toTypedArray())
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