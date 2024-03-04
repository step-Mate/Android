package jinproject.stepwalk.data.repositoryImpl

import jinproject.stepwalk.data.remote.dataSource.RemoteUserDataSource
import jinproject.stepwalk.data.remote.utils.stepMateDataFlow
import jinproject.stepwalk.domain.model.rank.UserStepRank
import jinproject.stepwalk.domain.model.user.UserDetailModel
import jinproject.stepwalk.data.local.datasource.BodyDataSource
import jinproject.stepwalk.data.local.datasource.UserDataSource
import jinproject.stepwalk.data.remote.dto.request.DesignationRequest
import jinproject.stepwalk.data.remote.dto.request.WithdrawRequest
import jinproject.stepwalk.data.remote.dto.request.toBodyRequest
import jinproject.stepwalk.data.remote.dto.response.rank.toUserStepRank
import jinproject.stepwalk.data.remote.dto.response.user.toDesignationModel
import jinproject.stepwalk.data.remote.dto.response.user.toUserDetailModel
import jinproject.stepwalk.data.remote.dto.response.user.toUserModel
import jinproject.stepwalk.data.remote.utils.stepMateDataFlow
import jinproject.stepwalk.domain.model.BodyData
import jinproject.stepwalk.domain.model.DesignationState
import jinproject.stepwalk.domain.model.User
import jinproject.stepwalk.domain.model.UserDetailModel
import jinproject.stepwalk.domain.model.UserStepRank
import jinproject.stepwalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

internal class UserRepositoryImpl @Inject constructor(
    private val remoteUserDataSource: RemoteUserDataSource,
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

    override fun withdrawAccount(password: String): Flow<Boolean> = stepMateDataFlow {
        val response = remoteUserDataSource.withdrawAccount(WithdrawRequest(password))
        response.code == 200
    }

    override fun selectDesignation(designation: String): Flow<Boolean> = stepMateDataFlow {
        val response = remoteUserDataSource.selectDesignation(DesignationRequest(designation))
        if (response.code == 200)
            userDataSource.setDesignation(designation)
        response.code == 200
    }

    override fun getDesignation(): Flow<DesignationState> = stepMateDataFlow {
        val response = remoteUserDataSource.getDesignations()
        response.toDesignationModel()
    }

    override fun getBodyData(): Flow<BodyData> =
        bodyDataSource.getBodyData()

    override fun setBodyData(bodyData: BodyData): Flow<Boolean> = stepMateDataFlow {
        val response = remoteUserDataSource.setBodyData(bodyData.toBodyRequest())
        if (response.code == 200)
            bodyDataSource.setBodyData(bodyData)
        response.code == 200
    }

    override suspend fun setBodyLocalData(bodyData: BodyData) =
        bodyDataSource.setBodyData(bodyData)

    override fun updateNickname(nickname: String): Flow<Boolean> = stepMateDataFlow {
        val response = remoteUserDataSource.updateNickname(nickname)
        if (response.code == 200)
            userDataSource.setNickname(nickname)
        response.code == 200
    }

    override fun getMyInfo(): Flow<User> = stepMateDataFlow {
        val response = remoteUserDataSource.getMyInfo()
        userDataSource.setUserData(response.toUserModel())
        response.toUserModel()
    }

    override fun getUserLocalInfo(): Flow<User> =
        userDataSource.getUserData()
}