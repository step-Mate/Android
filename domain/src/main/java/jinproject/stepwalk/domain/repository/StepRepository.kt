package jinproject.stepwalk.domain.repository

import jinproject.stepwalk.domain.model.StepData
import kotlinx.coroutines.flow.Flow

interface StepRepository {
    fun getStep(): Flow<StepData>
    suspend fun setTodayStep(today: Long)
    suspend fun setYesterdayStep(yesterday: Long)
    suspend fun setLastStep(last: Long)
}