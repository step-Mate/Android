package com.stepmate.data.remote.dataSource

import com.stepmate.domain.model.DesignationState
import com.stepmate.domain.model.mission.MissionList

internal interface RemoteMissionDataSource {
    suspend fun getMissionList(): List<MissionList>
    suspend fun selectDesignation(designation: String)
    suspend fun getDesignation(): DesignationState
    suspend fun completeMission(designation: String)
}