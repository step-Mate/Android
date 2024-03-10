package jinproject.stepwalk.domain.repository

import jinproject.stepwalk.domain.model.BodyData
import jinproject.stepwalk.domain.model.rank.UserStepRank
import jinproject.stepwalk.domain.model.user.User
import jinproject.stepwalk.domain.model.user.UserDetailModel
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getMyRank(): Flow<UserStepRank>
    fun getUserDetail(userName: String): Flow<UserDetailModel>
    suspend fun addFriend(userName: String)
    suspend fun deleteFriend(userName: String)
    suspend fun processFriendRequest(bool: Boolean, userName: String)
    fun getFriendRequest(): Flow<List<String>>
    suspend fun addStep(step: Int)
    suspend fun queryDailyStep(step: Int)
    fun withdrawAccount(password: String): Flow<Boolean>
    fun getBodyData(): Flow<BodyData>
    suspend fun setBodyData(bodyData: BodyData)
    suspend fun setBodyLocalData(bodyData: BodyData)
    suspend fun updateNickname(nickname: String)
    fun getMyInfo(): Flow<User>
}