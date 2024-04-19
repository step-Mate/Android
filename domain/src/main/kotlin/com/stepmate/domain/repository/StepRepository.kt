package com.stepmate.domain.repository

import kotlinx.coroutines.flow.Flow

interface StepRepository {
    fun getTodayStep(): Flow<Long>
    suspend fun setTodayStep(todayStep: Long)
    suspend fun setYesterdayStep(step: Long)
    fun getYesterdayStep(): Flow<Long>
}