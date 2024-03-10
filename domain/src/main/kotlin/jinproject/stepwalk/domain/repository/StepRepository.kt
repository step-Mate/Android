package jinproject.stepwalk.domain.repository

import kotlinx.coroutines.flow.Flow

interface StepRepository {
    fun getTodayStep(): Flow<Long>
    suspend fun setTodayStep(todayStep: Long)
}