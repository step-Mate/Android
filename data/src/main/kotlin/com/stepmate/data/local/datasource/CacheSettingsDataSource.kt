package com.stepmate.data.local.datasource

import kotlinx.coroutines.flow.Flow

interface CacheSettingsDataSource {
    suspend fun setStepGoal(step: Int)
    fun getStepGoal(): Flow<Int>

    suspend fun setTodayStep(todayStep: Long)
    fun getTodayStep(): Flow<Long>
}