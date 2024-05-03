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
    override suspend fun getMissionList(): List<MissionList> =
        missionDataSource.getMissionList()

    override suspend fun selectDesignation(designation: String) =
        missionDataSource.selectDesignation(designation)

    override fun getDesignation(): Flow<DesignationState> =
        missionDataSource.getDesignation()

    override suspend fun completeMission(designation: String) =
        missionDataSource.completeMission(designation)

    override suspend fun checkUpdateMission(missionList: List<MissionList>): List<String> =
        missionDataSource.checkUpdateMission(missionList)
}