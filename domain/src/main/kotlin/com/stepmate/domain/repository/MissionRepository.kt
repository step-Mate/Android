package com.stepmate.domain.repository

import com.stepmate.domain.model.DesignationState
import com.stepmate.domain.model.mission.MissionList
import kotlinx.coroutines.flow.Flow

interface MissionRepository {
    fun getAllMissionList(): Flow<List<MissionList>>
    fun getMissionList(title: String): Flow<MissionList>
    suspend fun updateMissionList(): List<String>
    suspend fun updateMission(achieved: Int)
    suspend fun selectDesignation(designation: String)
    fun getDesignation(): Flow<DesignationState>
    suspend fun checkUpdateMission(): List<String>
    suspend fun resetMissionTime()
}