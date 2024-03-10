package jinproject.stepwalk.domain.usecase.mission

import jinproject.stepwalk.domain.repository.MissionRepository
import javax.inject.Inject

class UpdateMissionList @Inject constructor(
    private val missionRepository: MissionRepository
){
    suspend operator fun invoke() = missionRepository.updateMissionList()
}