package jinproject.stepwalk.data.remote.api

import jinproject.stepwalk.data.remote.dto.response.rank.MonthRankBoardResponse
import jinproject.stepwalk.data.remote.dto.response.user.UserDetailResponse
import retrofit2.http.GET
import retrofit2.http.Path

internal interface UserApi {

    @GET("rank-board/my-info")
    suspend fun getMyRank(): MonthRankBoardResponse

    @GET("users/{nickname}")
    suspend fun getUserDetail(@Path("nickname") userName: String): UserDetailResponse
}