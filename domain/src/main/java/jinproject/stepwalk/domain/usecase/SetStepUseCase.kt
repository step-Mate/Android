package jinproject.stepwalk.domain.usecase

import jinproject.stepwalk.domain.repository.StepRepository
import javax.inject.Inject

class SetStepUseCase @Inject constructor(
    private val stepRepository: StepRepository
) {
    suspend fun setTodayStep(today: Long) {
        stepRepository.setTodayStep(today)
    }

    suspend fun setLastStep(last: Long) {
        stepRepository.setLastStep(last)
    }
}