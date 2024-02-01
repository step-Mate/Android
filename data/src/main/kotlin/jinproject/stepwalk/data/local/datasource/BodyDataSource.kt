package jinproject.stepwalk.data.local.datasource

import jinproject.stepwalk.domain.model.BodyData
import kotlinx.coroutines.flow.Flow

interface BodyDataSource {
    fun getBodyData() : Flow<BodyData>
    suspend fun setAge(age : Int)
    suspend fun setHeight(height : Int)
    suspend fun setWeight(weight : Int)
    suspend fun setBodyData(body : BodyData)
}