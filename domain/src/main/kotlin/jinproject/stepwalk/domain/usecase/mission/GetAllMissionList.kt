package jinproject.stepwalk.domain.usecase.mission

import jinproject.stepwalk.domain.model.mission.MissionList
import jinproject.stepwalk.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllMissionList @Inject constructor(
    private val missionRepository: MissionRepository
) {
    operator fun invoke(): Flow<List<MissionList>> = missionRepository.getAllMissionList().map {list ->
        list.map { mission ->
            MissionList(mission.title,mission.list.sortedBy { it.getMissionGoal() })
        }
    }
}