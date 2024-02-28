package jinproject.stepwalk.data.remote.dataSource

import jinproject.stepwalk.data.remote.dto.request.BodyRequest
import jinproject.stepwalk.data.remote.dto.request.DesignationRequest
import jinproject.stepwalk.data.remote.dto.request.WithdrawRequest
import jinproject.stepwalk.data.remote.dto.response.ApiResponse
import jinproject.stepwalk.data.remote.dto.response.rank.MonthRankBoardResponse
import jinproject.stepwalk.data.remote.dto.response.user.DesignationResponse
import jinproject.stepwalk.data.remote.dto.response.user.UserDetailResponse
import jinproject.stepwalk.data.remote.dto.response.user.UserInfoResponse

internal interface RemoteUserDataSource {
    suspend fun getMyRank(): MonthRankBoardResponse
    suspend fun getUserDetail(userName: String): UserDetailResponse
    suspend fun withdrawAccount(withdrawRequest: WithdrawRequest): ApiResponse<Nothing>
    suspend fun selectDesignation(designationRequest: DesignationRequest): ApiResponse<Nothing>
    suspend fun getDesignations(): List<DesignationResponse>
    suspend fun setBodyData(bodyRequest: BodyRequest): ApiResponse<Nothing>
    suspend fun updateNickname(nickname: String): ApiResponse<Nothing>
    suspend fun getMyInfo(): UserInfoResponse
}