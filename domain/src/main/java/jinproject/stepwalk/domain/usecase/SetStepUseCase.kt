package jinproject.stepwalk.domain.usecase

import jinproject.stepwalk.domain.repository.StepRepository
import javax.inject.Inject

class SetStepUseCase @Inject constructor(
    private val stepRepository: StepRepository
) {
    suspend operator fun invoke(step: Long) {
        stepRepository.setStep(step)
    }
}