package com.stepmate.data.local.datasource

import com.stepmate.data.local.database.entity.Mission
import com.stepmate.data.local.database.entity.MissionLeaf
import com.stepmate.data.local.database.entity.MissionType
import com.stepmate.domain.model.mission.MissionList
import kotlinx.coroutines.flow.Flow

interface LocalMissionDataSource {
    fun getAllMissionList(): Flow<List<MissionList>>
    fun getMissionList(title: String): Flow<MissionList>
    suspend fun addMissions(vararg mission: Mission)
    suspend fun addMissionLeafs(vararg missionLeaf: MissionLeaf)
    suspend fun synchronizationMission(designation : String, type: MissionType,  achieved: Int)
    suspend fun updateMission(type: MissionType, achieved: Int)
    suspend fun resetMissionTime()
}