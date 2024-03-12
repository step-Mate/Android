package com.stepmate.domain.repository

import com.stepmate.domain.model.DesignationState
import com.stepmate.domain.model.mission.MissionList
import com.stepmate.domain.model.mission.MissionType
import kotlinx.coroutines.flow.Flow

interface MissionRepository {
    fun getAllMissionList(): Flow<List<MissionList>>
    fun getMissionList(title: String): Flow<MissionList>
    suspend fun updateMissionList()
    suspend fun updateMission(achieved: Int)
    suspend fun completeMission(designation: String)
    suspend fun selectDesignation(designation: String)
    fun getDesignation(): Flow<DesignationState>
    fun getMissionAchieved(missionType: MissionType) : Flow<Int>
    suspend fun checkUpdateMission() : List<String>
}