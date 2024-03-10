package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.repository.MissionRepository
import javax.inject.Inject

class SelectDesignationUseCases @Inject constructor(
    private val missionRepository: MissionRepository
) {
    suspend operator fun invoke(designation: String) =
        missionRepository.selectDesignation(designation)
}