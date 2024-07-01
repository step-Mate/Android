package com.stepmate.domain.repository

import kotlinx.coroutines.flow.Flow

interface StepRepository {
    fun getTodayStep(): Flow<Long>
    suspend fun setTodayStep(todayStep: Long)
    suspend fun setYesterdayStep(step: Long)
    fun getYesterdayStep(): Flow<Long>
    fun getMissedTodayStepAfterReboot(): Flow<Long>
    suspend fun setMissedTodayStepAfterReboot(step: Long)
    fun getLatestEndEpochSecond(): Flow<Long>

    suspend fun setLatestEndEpochSecond(epochSecond: Long)
}