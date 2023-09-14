package jinproject.stepwalk.domain.repository

import kotlinx.coroutines.flow.Flow

interface StepRepository {
    fun getStep(): Flow<Int>
    suspend fun setStep(step: Long)
}