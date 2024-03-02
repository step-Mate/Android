package jinproject.stepwalk.data.remote.api

import jinproject.stepwalk.data.remote.dto.response.rank.MonthRankBoardResponse
import jinproject.stepwalk.data.remote.dto.response.user.UserDetailResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

internal interface UserApi {

    @GET("rank-board/my-info")
    suspend fun getMyRank(): MonthRankBoardResponse

    @GET("users/{nickname}")
    suspend fun getUserDetail(@Path("nickname") userName: String): UserDetailResponse

    @POST("users/{nickname}/friends")
    suspend fun addFriend(@Path("nickname") userName: String): Response<Any>

    @GET("users/friend-request")
    suspend fun getFriendRequest(): Response<List<Map<String, String>>>

    @POST("users/friend-request/{nickname}")
    suspend fun approveFriendRequest(@Path("nickname") userName: String): Response<Any>

    @POST("users/friend-request/{nickname}/denied")
    suspend fun denyFriendRequest(@Path("nickname") userName: String): Response<Any>
}