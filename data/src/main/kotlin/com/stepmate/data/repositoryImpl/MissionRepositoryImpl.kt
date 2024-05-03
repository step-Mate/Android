package com.stepmate.data.repositoryImpl

import com.stepmate.data.local.datasource.BodyDataSource
import com.stepmate.data.local.datasource.LocalMissionDataSource
import com.stepmate.data.remote.dataSource.RemoteMissionDataSource
import com.stepmate.domain.model.DesignationState
import com.stepmate.domain.model.mission.MissionList
import com.stepmate.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class MissionRepositoryImpl @Inject constructor(
    private val remoteMissionDataSource: RemoteMissionDataSource,
    private val localMissionDataSource: LocalMissionDataSource,
    private val bodyDataSource: BodyDataSource
) : MissionRepository {
    override suspend fun getLocalMissionList(): List<MissionList> =
        remoteMissionDataSource.getMissionList()

    override suspend fun selectDesignation(designation: String) =
        remoteMissionDataSource.selectDesignation(designation)

    override fun getDesignation(): Flow<DesignationState> =
        remoteMissionDataSource.getDesignation()

    override suspend fun completeMission(designation: String) =
        remoteMissionDataSource.completeMission(designation)

    override suspend fun checkUpdateMission(missionList: List<MissionList>): List<String> =
        remoteMissionDataSource.checkUpdateMission(missionList)

    override fun getAllLocalMissionList(): Flow<List<MissionList>> =
        localMissionDataSource.getAllMissionList()

    override fun getLocalMissionList(title: String): Flow<MissionList> =
        localMissionDataSource.getMissionList(title)

    override suspend fun updateMissionList(apiList: List<MissionList>) =
        localMissionDataSource.updateMissionList(apiList)

    override suspend fun updateMission(step: Int) =
        localMissionDataSource.updateMission(
            step = step,
            calories = bodyDataSource.getCalories(step).toInt()
        )

    override suspend fun resetMissionTime() =
        localMissionDataSource.resetMissionTime()
}