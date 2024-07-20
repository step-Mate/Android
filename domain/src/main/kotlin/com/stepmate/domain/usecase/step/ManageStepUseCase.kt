package com.stepmate.domain.usecase.step

import com.stepmate.domain.repository.StepRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ManageStepUseCaseImpl @Inject constructor(
    private val stepRepository: StepRepository,
) : ManageStepUseCase {

    override suspend fun setTodayStep(todayStep: Long) = stepRepository.setTodayStep(todayStep)

    override fun getTodayStep(): Flow<Long> = stepRepository.getTodayStep()

    override suspend fun setYesterdayStep(step: Long) = stepRepository.setYesterdayStep(step)

    override fun getYesterdayStep(): Flow<Long> = stepRepository.getYesterdayStep()

    override fun getMissedTodayStepAfterReboot(): Flow<Long> =
        stepRepository.getMissedTodayStepAfterReboot()

    override suspend fun setMissedTodayStepAfterReboot(step: Long) =
        stepRepository.setMissedTodayStepAfterReboot(step)

    override fun getLatestEndEpochSecond(): Flow<Long> = stepRepository.getLatestEndEpochSecond()

    override suspend fun setLatestEndEpochSecond(epochSecond: Long) = stepRepository.setLatestEndEpochSecond(epochSecond)
}

interface ManageStepUseCase {
    suspend fun setTodayStep(todayStep: Long)

    fun getTodayStep(): Flow<Long>

    suspend fun setYesterdayStep(step: Long)

    fun getYesterdayStep(): Flow<Long>

    fun getMissedTodayStepAfterReboot(): Flow<Long>

    suspend fun setMissedTodayStepAfterReboot(step: Long)

    fun getLatestEndEpochSecond(): Flow<Long>

    suspend fun setLatestEndEpochSecond(epochSecond: Long)
}

@Module
@InstallIn(SingletonComponent::class)
abstract class ManageStepUseCaseModule {
    @Binds
    abstract fun bindsManageStepUseCase(manageStepUseCaseImpl: ManageStepUseCaseImpl): ManageStepUseCase
}