package com.stepmate.data.local.datasource.impl

import com.stepmate.data.local.database.dao.MissionLocal
import com.stepmate.data.local.database.entity.Mission
import com.stepmate.data.local.database.entity.MissionLeaf
import com.stepmate.data.local.database.entity.MissionType
import com.stepmate.data.local.database.entity.toMissionDataList
import com.stepmate.data.local.datasource.LocalMissionDataSource
import com.stepmate.domain.model.mission.MissionList
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class LocalMissionDataSourceImpl @Inject constructor(
    private val missionLocal: MissionLocal,
) : LocalMissionDataSource {
    override fun getAllMissionList(): Flow<List<MissionList>> =
        missionLocal.getAllMissionList()
            .map { it.toMissionDataList().sortedBy { missions -> missions.title } }

    override fun getMissionList(title: String): Flow<MissionList> =
        missionLocal.getMissionList(title).map { it.toMissionDataList().first() }

    override suspend fun addMissions(vararg mission: Mission) =
        missionLocal.addMissions(*mission)

    override suspend fun addMissionLeafs(vararg missionLeaf: MissionLeaf) =
        missionLocal.addMissionLeafs(*missionLeaf)

    override suspend fun synchronizationMission(
        designation: String,
        type: MissionType,
        achieved: Int
    ) =
        missionLocal.synchronizationMissionAchieved(designation, type, achieved)

    override suspend fun updateMission(type: MissionType, achieved: Int) =
        withContext(Dispatchers.IO) {
            val localAchieved = getMissionAchieved(type).first() + achieved
            val timeAchieved = getMissionTimeAchieved(type).first() + achieved
            missionLocal.updateMissionAchieved(type, localAchieved)
            missionLocal.updateMissionTimeAchieved(type, timeAchieved)
        }

    override suspend fun resetMissionTime() =
        missionLocal.resetMissionTime()

    private fun getMissionAchieved(missionType: MissionType): Flow<Int> =
        missionLocal.getMissionAchieved(missionType)

    private fun getMissionTimeAchieved(missionType: MissionType): Flow<Int> =
        missionLocal.getMissionTimeAchieved(missionType)


}