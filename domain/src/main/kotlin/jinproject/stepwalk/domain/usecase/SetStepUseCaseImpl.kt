package jinproject.stepwalk.domain.usecase

import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import jinproject.stepwalk.domain.model.StepData
import jinproject.stepwalk.domain.repository.StepRepository
import javax.inject.Inject
import javax.inject.Singleton

class SetStepUseCaseImpl @Inject constructor(
    private val stepRepository: StepRepository
):SetStepUseCase {
    override suspend fun setTodayStep(today: Long) {
        stepRepository.setTodayStep(today)
    }

    override suspend fun setYesterdayStep(yesterday: Long) {
        stepRepository.setYesterdayStep(yesterday)
    }

    override suspend fun setLastStep(last: Long) {
        stepRepository.setLastStep(last)
    }

    override suspend operator fun invoke(stepData: StepData) {
        stepRepository.setStep(stepData)
    }
}

interface SetStepUseCase {
    suspend fun setTodayStep(today: Long)

    suspend fun setYesterdayStep(yesterday: Long)

    suspend fun setLastStep(last: Long)

    suspend operator fun invoke(stepData: StepData)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class StepUsecaseModule {

    @Singleton
    @Binds
    abstract fun bindsSetStepUseCase(setStepUseCaseImpl: SetStepUseCaseImpl): SetStepUseCase
}