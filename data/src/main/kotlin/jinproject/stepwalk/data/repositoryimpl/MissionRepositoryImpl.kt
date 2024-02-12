package jinproject.stepwalk.data.repositoryimpl

import jinproject.stepwalk.data.local.database.dao.MissionLocal
import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.model.mission.MissionList
import jinproject.stepwalk.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class MissionRepositoryImpl @Inject constructor(
    private val missionLocal: MissionLocal
) : MissionRepository {
    override suspend fun getAllMissionList(): Flow<ResponseState<List<MissionList>>> {
        return flow { }
    }

    override suspend fun getMissionList(title: String): Flow<ResponseState<MissionList>> {
        return flow { }
    }

    override suspend fun updateMissionList() {

    }

    override suspend fun updateMission() {

    }


}