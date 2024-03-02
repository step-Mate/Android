package jinproject.stepwalk.data.remote.api

import jinproject.stepwalk.data.remote.dto.request.BodyRequest
import jinproject.stepwalk.data.remote.dto.request.DesignationRequest
import jinproject.stepwalk.data.remote.dto.request.WithdrawRequest
import jinproject.stepwalk.data.remote.dto.response.ApiResponse
import jinproject.stepwalk.data.remote.dto.response.rank.MonthRankBoardResponse
import jinproject.stepwalk.data.remote.dto.response.user.DesignationResponse
import jinproject.stepwalk.data.remote.dto.response.user.UserDetailResponse
import jinproject.stepwalk.data.remote.dto.response.user.UserInfoResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

internal interface UserApi {

    @GET("rank-board/my-info")
    suspend fun getMyRank(): MonthRankBoardResponse

    @GET("users/{nickname}")
    suspend fun getUserDetail(@Path("nickname") userName: String): UserDetailResponse

    @POST("users/withdraw")
    suspend fun withdrawAccount(@Body withdrawRequest: WithdrawRequest): ApiResponse<Nothing>

    @PATCH("select-designation")
    suspend fun selectDesignation(@Body designationRequest: DesignationRequest): ApiResponse<Nothing>

    @GET("designations")
    suspend fun getDesignations(): List<DesignationResponse>

    @PATCH("users/body-info")
    suspend fun setBodyData(@Body bodyRequest: BodyRequest): ApiResponse<Nothing>

    @PATCH("users/nickname")
    suspend fun updateNickname(@Query("nickname") nickname: String): ApiResponse<Nothing>

    @GET("users/my-info")
    suspend fun getMyInfo(): UserInfoResponse

}