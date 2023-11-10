package jinproject.stepwalk.home

import jinproject.stepwalk.domain.model.StepData
import jinproject.stepwalk.domain.usecase.SetStepUseCase

class SetStepUseCaseFake: SetStepUseCase {
    var stepData: StepData? = null

    override suspend fun setTodayStep(today: Long) {
        throw IllegalArgumentException("사용하지 않음")
    }

    override suspend fun setYesterdayStep(yesterday: Long) {
        throw IllegalArgumentException("사용하지 않음")
    }

    override suspend fun setLastStep(last: Long) {
        throw IllegalArgumentException("사용하지 않음")
    }

    override suspend fun invoke(stepData: StepData) {
        this.stepData = stepData.copy()
    }
}