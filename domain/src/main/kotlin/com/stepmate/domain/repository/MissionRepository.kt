package com.stepmate.domain.repository

import com.stepmate.domain.model.DesignationState
import com.stepmate.domain.model.mission.MissionList
import kotlinx.coroutines.flow.Flow

interface MissionRepository {
    suspend fun getLocalMissionList(): List<MissionList>
    suspend fun selectDesignation(designation: String)
    fun getDesignation(): Flow<DesignationState>
    suspend fun completeMission(designation: String)
    suspend fun checkUpdateMission(missionList: List<MissionList>): List<String>
    fun getAllLocalMissionList(): Flow<List<MissionList>>
    fun getLocalMissionList(title: String): Flow<MissionList>
    suspend fun updateMissionList(apiList: List<MissionList>)
    suspend fun updateMission(step: Int)
    suspend fun resetMissionTime()
}