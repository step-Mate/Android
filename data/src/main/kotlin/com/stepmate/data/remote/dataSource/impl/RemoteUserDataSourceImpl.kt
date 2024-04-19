package com.stepmate.data.remote.dataSource.impl


import com.stepmate.data.di.RetrofitWithTokenModule
import com.stepmate.data.remote.api.UserApi
import com.stepmate.data.remote.dataSource.RemoteUserDataSource
import com.stepmate.data.remote.dto.request.BodyRequest
import com.stepmate.data.remote.dto.request.WithdrawRequest
import com.stepmate.data.remote.dto.response.ApiResponse
import com.stepmate.data.remote.dto.response.rank.toUserStepRank
import com.stepmate.data.remote.dto.response.user.toUserDetailModel
import com.stepmate.data.remote.dto.response.user.toUserModel
import com.stepmate.data.remote.utils.suspendAndCatchStepMateData
import com.stepmate.domain.model.rank.UserStepRank
import com.stepmate.domain.model.user.User
import com.stepmate.domain.model.user.UserDetailModel
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

    override suspend fun addStep(step: Int) {
        suspendAndCatchStepMateData(retrofit) {
            userApi.saveUserStep(step = step)
        }
    }

    override suspend fun queryDailyStep(step: Int) {
        suspendAndCatchStepMateData(retrofit) {
            userApi.saveUserDailyStep(step = step)
        }
    }

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