package com.stepmate.data.repositoryImpl

import com.stepmate.data.local.datasource.LocalMissionDataSource
import com.stepmate.domain.model.mission.MissionList
import com.stepmate.domain.repository.LocalMissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class LocalMissionRepositoryImpl @Inject constructor(
    private val localMissionDataSource: LocalMissionDataSource
) : LocalMissionRepository {
    override fun getAllMissionList(): Flow<List<MissionList>> =
        localMissionDataSource.getAllMissionList()

    override fun getMissionList(title: String): Flow<MissionList> =
        localMissionDataSource.getMissionList(title)

    override suspend fun updateMissionList(apiList: List<MissionList>) =
        localMissionDataSource.updateMissionList(apiList)

    override suspend fun updateMission(step: Int, calories: Int) =
        localMissionDataSource.updateMission(step, calories)

    override suspend fun resetMissionTime() =
        localMissionDataSource.resetMissionTime()
}