package jinproject.stepwalk.domain.repository

import jinproject.stepwalk.domain.model.BodyData
import jinproject.stepwalk.domain.model.DesignationState
import jinproject.stepwalk.domain.model.User
import jinproject.stepwalk.domain.model.UserDetailModel
import jinproject.stepwalk.domain.model.UserStepRank
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    fun getMyRank(): Flow<UserStepRank>
    fun getUserDetail(userName: String): Flow<UserDetailModel>
    fun withdrawAccount(password: String): Flow<Boolean>
    fun selectDesignation(designation: String): Flow<Boolean>
    fun getDesignation(): Flow<DesignationState>
    fun setBodyData(bodyData: BodyData): Flow<Boolean>
    fun updateNickname(nickname: String): Flow<Boolean>
    fun getMyInfo(): Flow<User>
}