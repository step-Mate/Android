package jinproject.stepwalk.data.remote.dataSource.impl

import jinproject.stepwalk.data.di.RetrofitWithTokenModule
import jinproject.stepwalk.data.remote.api.UserApi
import jinproject.stepwalk.data.remote.dataSource.RemoteUserDataSource
import jinproject.stepwalk.data.remote.dto.request.BodyRequest
import jinproject.stepwalk.data.remote.dto.request.WithdrawRequest
import jinproject.stepwalk.data.remote.dto.response.ApiResponse
import jinproject.stepwalk.data.remote.dto.response.rank.toUserStepRank
import jinproject.stepwalk.data.remote.dto.response.user.toUserDetailModel
import jinproject.stepwalk.data.remote.dto.response.user.toUserModel
import jinproject.stepwalk.data.remote.utils.suspendAndCatchStepMateData
import jinproject.stepwalk.domain.model.rank.UserStepRank
import jinproject.stepwalk.domain.model.user.User


import jinproject.stepwalk.domain.model.user.UserDetailModel
import retrofit2.Retrofit
import javax.inject.Inject

internal class RemoteUserDataSourceImpl @Inject constructor(
    private val userApi: UserApi,
    @RetrofitWithTokenModule.RetrofitWithInterceptor private val retrofit: Retrofit,
) : RemoteUserDataSource {
    override suspend fun getMyRank(): UserStepRank = userApi.getMyRank().toUserStepRank()
    override suspend fun getUserDetail(userName: String): UserDetailModel =
        userApi.getUserDetail(userName).toUserDetailModel()

    override suspend fun addFriend(userName: String) {
        suspendAndCatchStepMateData(retrofit) {
            userApi.addFriend(userName)
        }
    }

    override suspend fun deleteFriend(userName: String) {
        suspendAndCatchStepMateData(retrofit) {
            userApi.deleteFriend(userName)
        }
    }

    override suspend fun processFriendRequest(bool: Boolean, userName: String) {
        suspendAndCatchStepMateData(retrofit) {
            if (bool)
                userApi.approveFriendRequest(userName)
            else
                userApi.denyFriendRequest(userName)
        }
    }

    override suspend fun getFriendRequest(): List<String> =
        suspendAndCatchStepMateData(retrofit) {
            userApi.getFriendRequest()
        }?.map { data -> data["nickname"] ?: "" } ?: emptyList()

    override suspend fun withdrawAccount(withdrawRequest: WithdrawRequest): ApiResponse<Nothing> =
        userApi.withdrawAccount(withdrawRequest)

    override suspend fun setBodyData(bodyRequest: BodyRequest) {
        suspendAndCatchStepMateData(retrofit) {
            userApi.setBodyData(bodyRequest)
        }
    }

    override suspend fun updateNickname(nickname: String) {
        suspendAndCatchStepMateData(retrofit) {
            userApi.updateNickname(nickname)
        }
    }

    override suspend fun getMyInfo(): User = userApi.getMyInfo().toUserModel()
}