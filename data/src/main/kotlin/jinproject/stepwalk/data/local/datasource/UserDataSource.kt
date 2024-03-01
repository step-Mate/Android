package jinproject.stepwalk.data.local.datasource

import jinproject.stepwalk.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserDataSource {
    fun getUserData() : Flow<User>
    suspend fun setNickname(nickname : String)
    suspend fun setLevel(level : Int)
    suspend fun setDesignation(designation : String)
    suspend fun setUserData(user: User)
    suspend fun clearUser()
}