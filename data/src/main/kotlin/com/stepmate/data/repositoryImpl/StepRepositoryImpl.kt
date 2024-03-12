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
}