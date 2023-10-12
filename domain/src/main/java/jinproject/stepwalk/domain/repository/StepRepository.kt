package jinproject.stepwalk.domain.repository

import kotlinx.coroutines.flow.Flow

interface StepRepository {
    fun getStep(): Flow<Array<Long>>
    suspend fun setTodayStep(today: Long)
    suspend fun setYesterdayStep(yesterday: Long)
    suspend fun setLastStep(last: Long)
}