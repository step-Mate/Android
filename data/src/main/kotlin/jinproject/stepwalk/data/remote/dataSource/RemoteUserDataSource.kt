package jinproject.stepwalk.data.remote.dataSource

import jinproject.stepwalk.domain.model.rank.UserStepRank
import jinproject.stepwalk.domain.model.user.UserDetailModel

internal interface RemoteUserDataSource {
    suspend fun getMyRank(): UserStepRank
    suspend fun getUserDetail(userName: String): UserDetailModel
    suspend fun addFriend(userName: String)
    suspend fun deleteFriend(userName: String)
    suspend fun processFriendRequest(bool: Boolean, userName: String)
    suspend fun getFriendRequest(): List<String>
    suspend fun addStep(step: Int)
    suspend fun queryDailyStep(step: Int)
}