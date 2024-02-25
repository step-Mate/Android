package jinproject.stepwalk.data.remote.dataSource

import jinproject.stepwalk.data.remote.dto.response.rank.MonthRankBoardResponse
import jinproject.stepwalk.data.remote.dto.response.user.UserDetailResponse

internal interface RemoteUserDataSource {
    suspend fun getMyRank(): MonthRankBoardResponse
    suspend fun getUserDetail(userName: String): UserDetailResponse
}