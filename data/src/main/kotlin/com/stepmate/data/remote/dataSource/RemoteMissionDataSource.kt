package com.stepmate.data.remote.dataSource

import com.stepmate.domain.model.mission.MissionCommon

internal interface RemoteMissionDataSource {
    suspend fun getMissionList(): Map<String, List<MissionCommon>>
    suspend fun selectDesignation(designation: String)
    suspend fun getDesignation(): List<String>
    suspend fun completeMission(designation: String)
}