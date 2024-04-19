package com.stepmate.data.repositoryImpl

import com.stepmate.data.local.datasource.CacheSettingsDataSource
import com.stepmate.domain.repository.SettingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingsRepositoryImpl @Inject constructor(
    private val cacheSettingsDataSource: CacheSettingsDataSource,
): SettingsRepository {
    override suspend fun setStepGoal(step: Int) {
        cacheSettingsDataSource.setStepGoal(step)
    }

    override fun getStepGoal(): Flow<Int> = cacheSettingsDataSource.getStepGoal()
}