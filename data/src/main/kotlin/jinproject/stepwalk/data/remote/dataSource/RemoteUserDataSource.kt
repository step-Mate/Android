package jinproject.stepwalk.data.remote.dataSource

import jinproject.stepwalk.data.remote.dto.request.BodyRequest
import jinproject.stepwalk.data.remote.dto.request.DesignationRequest
import jinproject.stepwalk.data.remote.dto.request.WithdrawRequest
import jinproject.stepwalk.data.remote.dto.response.ApiResponse
import jinproject.stepwalk.domain.model.DesignationState
import jinproject.stepwalk.domain.model.rank.UserStepRank
import jinproject.stepwalk.domain.model.user.User
import jinproject.stepwalk.domain.model.user.UserDetailModel

internal interface RemoteUserDataSource {
    suspend fun getMyRank(): UserStepRank
    suspend fun getUserDetail(userName: String): UserDetailModel
    suspend fun addFriend(userName: String)
    suspend fun deleteFriend(userName: String)
    suspend fun processFriendRequest(bool: Boolean, userName: String)
    suspend fun getFriendRequest(): List<String>
    suspend fun withdrawAccount(withdrawRequest: WithdrawRequest) : ApiResponse<Nothing>
    suspend fun selectDesignation(designationRequest: DesignationRequest)
    suspend fun getDesignations(): DesignationState
    suspend fun setBodyData(bodyRequest: BodyRequest)
    suspend fun updateNickname(nickname: String)
    suspend fun getMyInfo(): User
}