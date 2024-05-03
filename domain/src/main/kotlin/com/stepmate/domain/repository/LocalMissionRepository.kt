package com.stepmate.domain.repository

import com.stepmate.domain.model.mission.MissionList
import kotlinx.coroutines.flow.Flow

interface LocalMissionRepository {
    fun getAllMissionList(): Flow<List<MissionList>>
    fun getMissionList(title: String): Flow<MissionList>
    suspend fun updateMissionList(apiList : List<MissionList>)
    suspend fun updateMission(step: Int, calories : Int)
    suspend fun resetMissionTime()
}