package jinproject.stepwalk.data.local.datasource

import kotlinx.coroutines.flow.Flow

interface CurrentAuthDataSource {
    fun getAccessToken() : Flow<String>
    fun getRefreshToken() : Flow<String>
    suspend fun setAccessToken(token : String)
    suspend fun setRefreshToken(token: String)
    suspend fun setToken(accessToken: String, refreshToken : String)
    suspend fun clearAuth()
}