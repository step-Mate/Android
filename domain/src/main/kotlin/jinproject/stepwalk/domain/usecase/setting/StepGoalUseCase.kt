package jinproject.stepwalk.domain.usecase.setting

import jinproject.stepwalk.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StepGoalUseCase @Inject constructor(
    private val settingsRepository: SettingsRepository
) {
    suspend fun setStep(step: Int) = settingsRepository.setStepGoal(step)

    fun getStep(): Flow<Int> = settingsRepository.getStepGoal()

}