package jinproject.stepwalk.domain.usecase.mission

import jinproject.stepwalk.domain.repository.MissionRepository
import javax.inject.Inject

class CheckUpdateMissionUseCases @Inject constructor(
    private val missionRepository: MissionRepository
){
    suspend operator fun invoke() : List<String> = missionRepository.checkUpdateMission()
}