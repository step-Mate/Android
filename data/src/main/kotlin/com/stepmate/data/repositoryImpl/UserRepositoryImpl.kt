package com.stepmate.data.repositoryImpl

import com.stepmate.data.local.datasource.BodyDataSource
import com.stepmate.data.remote.dataSource.RemoteUserDataSource
import com.stepmate.data.remote.dto.request.WithdrawRequest
import com.stepmate.data.remote.dto.request.toBodyRequest
import com.stepmate.data.remote.utils.stepMateDataFlow
import com.stepmate.domain.model.BodyData
import com.stepmate.domain.model.rank.UserStepRank
import com.stepmate.domain.model.user.User
import com.stepmate.domain.model.user.UserDetailModel
import com.stepmate.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val remoteUserDataSource: RemoteUserDataSource,
    private val cacheBodyDataSource: BodyDataSource,
) : UserRepository {
    override fun getMyRank(): Flow<UserStepRank> = stepMateDataFlow {
        remoteUserDataSource.getMyRank()
    }

    override fun getUserDetail(userName: String): Flow<UserDetailModel> = stepMateDataFlow {
        remoteUserDataSource.getUserDetail(userName)
    }

    override suspend fun addFriend(userName: String) {
        remoteUserDataSource.addFriend(userName)
    }

    override suspend fun deleteFriend(userName: String) {
        remoteUserDataSource.deleteFriend(userName)
    }

    override suspend fun processFriendRequest(bool: Boolean, userName: String) {
        remoteUserDataSource.processFriendRequest(bool, userName)
    }

    override fun getFriendRequest(): Flow<List<String>> = stepMateDataFlow {
        remoteUserDataSource.getFriendRequest()
    }

    override suspend fun addStep(step: Int) {
        remoteUserDataSource.addStep(step = step)
    }

    override suspend fun queryDailyStep(step: Int) {
        remoteUserDataSource.queryDailyStep(step)
    }

    override fun withdrawAccount(password: String): Flow<Boolean> = stepMateDataFlow {
        val response = remoteUserDataSource.withdrawAccount(WithdrawRequest(password))
        response.code == 200
    }

    override fun getBodyData(): Flow<BodyData> =
        cacheBodyDataSource.getBodyData()

    override suspend fun setBodyData(bodyData: BodyData) {
        remoteUserDataSource.setBodyData(bodyData.toBodyRequest())
        cacheBodyDataSource.setBodyData(bodyData)
    }

    override suspend fun setBodyLocalData(bodyData: BodyData) =
        cacheBodyDataSource.setBodyData(bodyData)

    override suspend fun updateNickname(nickname: String) {
        remoteUserDataSource.updateNickname(nickname)
    }

    override fun getMyInfo(): Flow<User> = stepMateDataFlow {
        remoteUserDataSource.getMyInfo()
    }

    override suspend fun setBodyAge(age: Int) {
        cacheBodyDataSource.setAge(age)
    }

    override suspend fun setBodyWeight(weight: Int) {
        cacheBodyDataSource.setWeight(weight)
    }

    override suspend fun setBodyHeight(height: Int) {
        cacheBodyDataSource.setHeight(height)
    }

    override suspend fun getCalories(step: Int): Double =
        cacheBodyDataSource.getCalories(step)

}