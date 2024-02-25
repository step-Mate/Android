package jinproject.stepwalk.data.remote.dataSource.impl

import jinproject.stepwalk.data.remote.api.UserApi
import jinproject.stepwalk.data.remote.dataSource.RemoteUserDataSource
import jinproject.stepwalk.data.remote.dto.response.rank.MonthRankBoardResponse
import jinproject.stepwalk.data.remote.dto.response.user.UserDetailResponse
import javax.inject.Inject

internal class RemoteUserDataSourceImpl @Inject constructor(
    private val userApi: UserApi,
): RemoteUserDataSource {
    override suspend fun getMyRank(): MonthRankBoardResponse = userApi.getMyRank()
    override suspend fun getUserDetail(userName: String): UserDetailResponse = userApi.getUserDetail(userName)

}