package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.model.DesignationState
import jinproject.stepwalk.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDesignationsUseCases @Inject constructor(
    private val missionRepository: MissionRepository
) {
    operator fun invoke(): Flow<DesignationState> = missionRepository.getDesignation()
}