package com.stepmate.domain.repository

import com.stepmate.domain.model.DesignationState
import com.stepmate.domain.model.mission.MissionList
import kotlinx.coroutines.flow.Flow

interface MissionRepository {
    suspend fun selectDesignation(designation: String)
    fun getDesignation(): Flow<DesignationState>
    suspend fun checkCompleteMission(): List<String>
    fun getAllLocalMissionList(): Flow<List<MissionList>>
    fun getLocalMissionList(title: String): Flow<MissionList>
    suspend fun synchronizationMissionList()
    suspend fun updateStepAndCalories(step: Int)
    suspend fun resetMissionTime()
}