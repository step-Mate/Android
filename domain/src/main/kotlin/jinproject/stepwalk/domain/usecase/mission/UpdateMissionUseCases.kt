package jinproject.stepwalk.domain.usecase.mission

import jinproject.stepwalk.domain.repository.MissionRepository
import javax.inject.Inject

class UpdateMissionUseCases @Inject constructor(
    private val missionRepository: MissionRepository
) {
    suspend operator fun invoke(achieved: Int) = missionRepository.updateMission(achieved)
}