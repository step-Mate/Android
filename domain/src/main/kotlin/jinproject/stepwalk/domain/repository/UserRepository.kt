package jinproject.stepwalk.domain.repository

import jinproject.stepwalk.domain.model.user.UserDetailModel
import jinproject.stepwalk.domain.model.rank.UserStepRank
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getMyRank(): Flow<UserStepRank>
    fun getUserDetail(userName: String): Flow<UserDetailModel>
    suspend fun addFriend(userName: String)
    suspend fun processFriendRequest(bool: Boolean, userName: String)
    fun getFriendRequest(): Flow<List<String>>
}