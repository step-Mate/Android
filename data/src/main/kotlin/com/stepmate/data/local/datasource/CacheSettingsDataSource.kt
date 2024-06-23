package com.stepmate.data.local.datasource

import kotlinx.coroutines.flow.Flow

interface CacheSettingsDataSource {
    suspend fun setStepGoal(step: Int)
    fun getStepGoal(): Flow<Int>
    suspend fun setTodayStep(todayStep: Long)
    fun getTodayStep(): Flow<Long>
    suspend fun setYesterdayStep(step: Long)
    fun getYesterdayStep(): Flow<Long>
    fun getMissedTodayStepAfterReboot(): Flow<Long>
    suspend fun setMissedTodayStepAfterReboot(step: Long)
    fun getLatestEndEpochSecond(): Flow<Long>

    suspend fun setLatestEndTime(epochSecond: Long)
}