package com.stepmate.home.fake

import com.stepmate.domain.model.StepData
import com.stepmate.domain.usecase.step.ManageStepUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

internal class ManageStepUseCaseFake : ManageStepUseCase {
    var stepData = StepData.getInitValues()
    var latestEpochSecond = 0L

    override suspend fun setTodayStep(todayStep: Long) {
        stepData = stepData.copy(current = todayStep)
    }

    override fun getTodayStep(): Flow<Long> = flow {
        emit(stepData.current)
    }

    override suspend fun setYesterdayStep(step: Long) {
        stepData = stepData.copy(yesterday = step)
    }

    override fun getYesterdayStep(): Flow<Long> = flow {
        emit(stepData.yesterday)
    }

    override fun getMissedTodayStepAfterReboot(): Flow<Long> = flow {
        emit(stepData.missedTodayStepAfterReboot)
    }

    override suspend fun setMissedTodayStepAfterReboot(step: Long) {
        stepData = stepData.copy(missedTodayStepAfterReboot = step)
    }

    override fun getLatestEndEpochSecond(): Flow<Long> = flow { emit(latestEpochSecond) }

    override suspend fun setLatestEndTime(epochSecond: Long) {
        latestEpochSecond = epochSecond
    }

}