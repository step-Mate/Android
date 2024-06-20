package com.stepmate.data.repositoryImpl

import com.stepmate.data.local.datasource.CacheSettingsDataSource
import com.stepmate.domain.repository.StepRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class StepRepositoryImpl @Inject constructor(
    private val cacheSettingsDataSource: CacheSettingsDataSource,
): StepRepository  {
    override fun getTodayStep(): Flow<Long> =  cacheSettingsDataSource.getTodayStep()

    override suspend fun setTodayStep(todayStep: Long) {
        cacheSettingsDataSource.setTodayStep(todayStep)
    }

    override suspend fun setYesterdayStep(step: Long) {
        cacheSettingsDataSource.setYesterdayStep(step)
    }

    override fun getYesterdayStep(): Flow<Long> = cacheSettingsDataSource.getYesterdayStep()
    override fun getMissedTodayStepAfterReboot(): Flow<Long> = cacheSettingsDataSource.getMissedTodayStepAfterReboot()

    override suspend fun setMissedTodayStepAfterReboot(step: Long) {
        cacheSettingsDataSource.setMissedTodayStepAfterReboot(step)
    }
}