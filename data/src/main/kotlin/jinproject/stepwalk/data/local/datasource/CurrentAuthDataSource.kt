package jinproject.stepwalk.data.local.datasource

import jinproject.stepwalk.domain.model.CurrentAuth
import kotlinx.coroutines.flow.Flow

interface CurrentAuthDataSource {
    fun getCurrentAuth() : Flow<CurrentAuth>
    suspend fun setId(id : String)
    suspend fun setAccessToken(token : String)
    suspend fun setCurrentAuth(auth : CurrentAuth)
    suspend fun clearAuth()
}