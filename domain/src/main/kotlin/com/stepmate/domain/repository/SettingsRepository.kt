package com.stepmate.domain.repository

import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    suspend fun setStepGoal(step: Int)
    fun getStepGoal(): Flow<Int>
}