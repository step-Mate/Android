package com.stepmate.data.remote.dataSource

import com.stepmate.domain.model.DesignationState
import com.stepmate.domain.model.mission.MissionList
import kotlinx.coroutines.flow.Flow

internal interface RemoteMissionDataSource {
    suspend fun getMissionList(): List<MissionList>
    suspend fun selectDesignation(designation: String)
    fun getDesignation(): Flow<DesignationState>
    suspend fun completeMission(designation: String)
    suspend fun checkUpdateMission(missionList: List<MissionList>): List<String>
}