package jinproject.stepwalk.data

import jinproject.stepwalk.data.local.datasource.CurrentAuthDataSource
import jinproject.stepwalk.data.local.datasource.UserDataSource
import jinproject.stepwalk.domain.model.CurrentAuth
import jinproject.stepwalk.domain.model.UserData
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class CurrentAuthDataSourceTest : CurrentAuthDataSource {
    override fun getCurrentAuth(): Flow<CurrentAuth> {
        return flow {
            emit(CurrentAuth("", token = "2312490"))
        }
    }

    override suspend fun setId(id: String) {
    }
    override suspend fun setAccessToken(token: String) {
    }
    override suspend fun setCurrentAuth(auth: CurrentAuth) {
    }
    override suspend fun clearAuth() {
    }

}

class UserDataSourceTest : UserDataSource {
    override suspend fun setUserData(userData: UserData, token: String) {
    }
    override suspend fun updateUserData(userData: UserData, token: String) {
    }
    override suspend fun setRefreshToken(id: String, token: String) {
    }
    override fun getUserData(id: String): Flow<List<UserData>> {
        return flow {
            emit(listOf(
                UserData(
                    id = "testid",
                    password = "",
                    nickname = "test",
                    email = "tset123@naver.com",
                    age = 12,
                    height = 177,
                    weight = 67
            )
            ))
        }
    }
}