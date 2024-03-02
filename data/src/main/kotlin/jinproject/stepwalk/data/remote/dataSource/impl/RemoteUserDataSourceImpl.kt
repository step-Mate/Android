package jinproject.stepwalk.data.remote.dataSource.impl

import jinproject.stepwalk.data.remote.api.UserApi
import jinproject.stepwalk.data.remote.dataSource.RemoteUserDataSource
import jinproject.stepwalk.data.remote.dto.request.BodyRequest
import jinproject.stepwalk.data.remote.dto.request.DesignationRequest
import jinproject.stepwalk.data.remote.dto.request.WithdrawRequest
import jinproject.stepwalk.data.remote.dto.response.ApiResponse
import jinproject.stepwalk.data.remote.dto.response.rank.MonthRankBoardResponse
import jinproject.stepwalk.data.remote.dto.response.user.DesignationResponse
import jinproject.stepwalk.data.remote.dto.response.user.UserDetailResponse
import jinproject.stepwalk.data.remote.dto.response.user.UserInfoResponse
import javax.inject.Inject

internal class RemoteUserDataSourceImpl @Inject constructor(
    private val userApi: UserApi,
) : RemoteUserDataSource {
    override suspend fun getMyRank(): MonthRankBoardResponse = userApi.getMyRank()
    override suspend fun getUserDetail(userName: String): UserDetailResponse =
        userApi.getUserDetail(userName)

    override suspend fun withdrawAccount(withdrawRequest: WithdrawRequest): ApiResponse<Nothing> =
        userApi.withdrawAccount(withdrawRequest)

    override suspend fun selectDesignation(designationRequest: DesignationRequest): ApiResponse<Nothing> =
        userApi.selectDesignation(designationRequest)

    override suspend fun getDesignations(): List<DesignationResponse> = userApi.getDesignations()

    override suspend fun setBodyData(bodyRequest: BodyRequest): ApiResponse<Nothing> =
        userApi.setBodyData(bodyRequest)

    override suspend fun updateNickname(nickname: String): ApiResponse<Nothing> =
        userApi.updateNickname(nickname)

    override suspend fun getMyInfo(): UserInfoResponse =
        userApi.getMyInfo()
}