package jinproject.stepwalk.data.local.datasource.impl

import jinproject.stepwalk.data.local.database.dao.UserLocal
import jinproject.stepwalk.data.local.datasource.UserDataSource
import jinproject.stepwalk.data.remote.mapper.toUser
import jinproject.stepwalk.data.remote.mapper.toUserData
import jinproject.stepwalk.domain.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class UserDataSourceImpl @Inject constructor(
    private val userService : UserLocal
) : UserDataSource{
    override fun getUserData(id: String): Flow<List<UserData>> =
        userService.getUserData(id).map { list -> list.map { it.toUserData() } }

    override suspend fun setUserData(userData: UserData, token: String) =
        userService.setUserData(userData.toUser(token))

    override suspend fun updateUserData(userData: UserData, token: String) =
        userService.updateUserData(userData.toUser(token))

    override suspend fun setRefreshToken(id: String, token: String) =
        userService.setRefreshToken(id, token)

}