package jinproject.stepwalk.data.local.datasource

import jinproject.stepwalk.domain.model.UserData
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    suspend fun setUserData(userData: UserData,token : String)
    suspend fun updateUserData(userData: UserData,token: String)
    suspend fun setRefreshToken(id: String, token : String)
    fun getUserData(id: String) : Flow<List<UserData>>
}