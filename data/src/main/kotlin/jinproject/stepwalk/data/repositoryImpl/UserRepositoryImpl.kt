package jinproject.stepwalk.data.repositoryImpl

import jinproject.stepwalk.data.remote.dataSource.RemoteUserDataSource
import jinproject.stepwalk.data.remote.utils.stepMateDataFlow
import jinproject.stepwalk.domain.model.rank.UserStepRank
import jinproject.stepwalk.domain.model.user.UserDetailModel
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
        remoteUserDataSource.addUser(userName)
    }

    override suspend fun processFriendRequest(bool: Boolean, userName: String) {
        remoteUserDataSource.processFriendRequest(bool, userName)
    }

    override fun getFriendRequest(): Flow<List<String>> = stepMateDataFlow {
        remoteUserDataSource.getFriendRequest()
    }
}