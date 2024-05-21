package com.stepmate.domain.repository

import com.stepmate.domain.model.mission.MissionCommon
import kotlinx.coroutines.flow.Flow

interface MissionRepository {
    suspend fun selectDesignation(designation: String)
    fun getDesignation(): Flow<List<String>>
    suspend fun checkCompleteMission(): List<String>
    fun getAllLocalMissionList(): Flow<Map<String, List<MissionCommon>>>
    fun getLocalMissionList(title: String): Flow<List<MissionCommon>>
    suspend fun synchronizationMissionList()
    suspend fun updateStepAndCalories(step: Int)
    suspend fun resetMissionTime()
}