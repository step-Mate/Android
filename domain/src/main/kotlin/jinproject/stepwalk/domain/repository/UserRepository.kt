package jinproject.stepwalk.domain.repository

import jinproject.stepwalk.domain.model.user.UserDetailModel
import jinproject.stepwalk.domain.model.rank.UserStepRank
import jinproject.stepwalk.domain.model.BodyData
import jinproject.stepwalk.domain.model.DesignationState
import jinproject.stepwalk.domain.model.User
import jinproject.stepwalk.domain.model.UserDetailModel
import jinproject.stepwalk.domain.model.UserStepRank
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getMyRank(): Flow<UserStepRank>
    fun getUserDetail(userName: String): Flow<UserDetailModel>
    suspend fun addFriend(userName: String)
    suspend fun deleteFriend(userName: String)
    suspend fun processFriendRequest(bool: Boolean, userName: String)
    fun getFriendRequest(): Flow<List<String>>
    fun withdrawAccount(password: String): Flow<Boolean>
    fun selectDesignation(designation: String): Flow<Boolean>
    fun getDesignation(): Flow<DesignationState>
    fun getBodyData(): Flow<BodyData>
    fun setBodyData(bodyData: BodyData): Flow<Boolean>
    suspend fun setBodyLocalData(bodyData: BodyData)
    fun updateNickname(nickname: String): Flow<Boolean>
    fun getMyInfo(): Flow<User>
    fun getUserLocalInfo(): Flow<User>
}