package jinproject.stepwalk.domain.repository

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.model.mission.MissionList
import kotlinx.coroutines.flow.Flow

interface MissionRepository {
    suspend fun getAllMissionList() : Flow<ResponseState<List<MissionList>>>
    suspend fun getMissionList(title : String) : Flow<ResponseState<MissionList>>
    suspend fun updateMissionList()
    suspend fun updateMission()
}