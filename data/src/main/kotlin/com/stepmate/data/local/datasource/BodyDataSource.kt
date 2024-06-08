package com.stepmate.data.local.datasource

import com.stepmate.domain.model.BodyData
import kotlinx.coroutines.flow.Flow

interface BodyDataSource {
    fun getBodyData(): Flow<BodyData>
    suspend fun setAge(age: Int)
    suspend fun setHeight(height: Int)
    suspend fun setWeight(weight: Int)
    suspend fun setBodyData(body: BodyData)
    suspend fun getCalories(step : Int) : Double
}