package jinproject.stepwalk.domain.usecase

import jinproject.stepwalk.domain.model.StepData
import jinproject.stepwalk.domain.repository.StepRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetStepUseCase @Inject constructor(
    private val stepRepository: StepRepository
) {
    operator fun invoke(): Flow<StepData> = stepRepository.getStep()
}