package com.stepmate.data.repositoryImpl

import com.stepmate.data.remote.dataSource.MissionDataSource
import com.stepmate.domain.model.DesignationState
import com.stepmate.domain.model.mission.MissionList
import com.stepmate.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class MissionRepositoryImpl @Inject constructor(
    private val missionDataSource: MissionDataSource
) : MissionRepository {
    override fun getAllMissionList(): Flow<List<MissionList>> =
        missionDataSource.getAllMissionList()

    override fun getMissionList(title: String): Flow<MissionList> =
        missionDataSource.getMissionList(title)

    override suspend fun updateMissionList(): List<String> =
        missionDataSource.updateMissionList()

    override suspend fun updateMission(achieved: Int) =
        missionDataSource.updateMission(achieved)

    override suspend fun selectDesignation(designation: String) =
        missionDataSource.selectDesignation(designation)

    override fun getDesignation(): Flow<DesignationState> =
        missionDataSource.getDesignation()

    override suspend fun checkUpdateMission(): List<String> =
        missionDataSource.checkUpdateMission()

    override suspend fun resetMissionTime() =
        missionDataSource.resetMissionTime()

}