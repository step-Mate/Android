package jinproject.stepwalk.domain.usecase.mission

import jinproject.stepwalk.domain.model.mission.MissionType
import jinproject.stepwalk.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMissionAchievedUseCases @Inject constructor(
    private val missionRepository: MissionRepository
) {
    operator fun invoke(missionType: MissionType) : Flow<Int> = missionRepository.getMissionAchieved(missionType)
}