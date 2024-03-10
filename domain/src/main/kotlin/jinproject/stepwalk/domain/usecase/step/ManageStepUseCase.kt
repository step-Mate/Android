package jinproject.stepwalk.domain.usecase.step

import jinproject.stepwalk.domain.repository.StepRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageStepUseCase @Inject constructor(
    private val stepRepository: StepRepository,
) {

    suspend fun setTodayStep(todayStep: Long) = stepRepository.setTodayStep(todayStep)

    fun getTodayStep(): Flow<Long> = stepRepository.getTodayStep()
}