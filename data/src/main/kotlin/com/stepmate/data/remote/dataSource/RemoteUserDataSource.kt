package com.stepmate.data.remote.dataSource

import com.stepmate.data.remote.dto.request.BodyRequest
import com.stepmate.data.remote.dto.request.WithdrawRequest
import com.stepmate.data.remote.dto.response.ApiResponse
import com.stepmate.domain.model.rank.UserStepRank
import com.stepmate.domain.model.user.User
import com.stepmate.domain.model.user.UserDetailModel

internal interface RemoteUserDataSource {
    suspend fun getMyRank(): UserStepRank
    suspend fun getUserDetail(userName: String): UserDetailModel
    suspend fun addFriend(userName: String)
    suspend fun deleteFriend(userName: String)
    suspend fun processFriendRequest(bool: Boolean, userName: String)
    suspend fun getFriendRequest(): List<String>
    suspend fun addStep(step: Int)
    suspend fun queryDailyStep(step: Int)
    suspend fun withdrawAccount(withdrawRequest: WithdrawRequest): ApiResponse<Nothing>
    suspend fun setBodyData(bodyRequest: BodyRequest)
    suspend fun updateNickname(nickname: String)
    suspend fun getMyInfo(): User
}