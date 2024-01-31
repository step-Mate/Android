package jinproject.stepwalk.data.local.datasource

import jinproject.stepwalk.domain.model.BodyData
import kotlinx.coroutines.flow.Flow

interface CurrentAuthDataSource {
    fun getNickname() : Flow<String>
    fun getAccessToken() : Flow<String>
    fun getRefreshToken() : Flow<String>
    fun getBodyData() : Flow<BodyData>
    suspend fun setAccessToken(token : String)
    suspend fun setRefreshToken(token: String)
    suspend fun setToken(accessToken: String, refreshToken : String)
    suspend fun setBodyData(body : BodyData)
    suspend fun setNickname(nickname: String)
    suspend fun setAuthData(nickname: String,accessToken: String, refreshToken : String)
    suspend fun clearAuth()
}