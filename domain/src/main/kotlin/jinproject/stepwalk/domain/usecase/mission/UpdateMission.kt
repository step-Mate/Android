package jinproject.stepwalk.domain.usecase.mission

import jinproject.stepwalk.domain.model.mission.MissionType
import jinproject.stepwalk.domain.repository.MissionRepository
import javax.inject.Inject

class UpdateMission @Inject constructor(
    private val missionRepository: MissionRepository
){
    suspend operator fun invoke(type: MissionType, achieved : Int) = missionRepository.updateMission(type, achieved)
}