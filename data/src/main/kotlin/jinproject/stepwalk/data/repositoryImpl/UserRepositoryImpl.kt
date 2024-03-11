package jinproject.stepwalk.data.repositoryImpl

import jinproject.stepwalk.data.local.datasource.BodyDataSource
import jinproject.stepwalk.data.remote.dataSource.RemoteUserDataSource
import jinproject.stepwalk.data.remote.dto.request.WithdrawRequest
import jinproject.stepwalk.data.remote.dto.request.toBodyRequest
import jinproject.stepwalk.data.remote.utils.stepMateDataFlow
import jinproject.stepwalk.domain.model.BodyData
import jinproject.stepwalk.domain.model.rank.UserStepRank
import jinproject.stepwalk.domain.model.user.User
import jinproject.stepwalk.domain.model.user.UserDetailModel
import jinproject.stepwalk.domain.repository.UserRepository
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
}