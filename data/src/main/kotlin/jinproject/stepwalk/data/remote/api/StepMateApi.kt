package jinproject.stepwalk.data.remote.api

import jinproject.stepwalk.data.remote.dto.request.AccountRequest
import jinproject.stepwalk.data.remote.dto.request.DuplicationIdRequest
import jinproject.stepwalk.data.remote.dto.request.DuplicationNicknameRequest
import jinproject.stepwalk.data.remote.dto.request.SignUpRequest
import jinproject.stepwalk.data.remote.dto.response.ApiResponse
import jinproject.stepwalk.data.remote.dto.response.JwtToken
import jinproject.stepwalk.data.remote.dto.response.Token
import jinproject.stepwalk.data.remote.dto.response.UserId
import jinproject.stepwalk.domain.model.ResponseState
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface StepMateApi {

    @POST("users/id/validation")
    suspend fun checkDuplicationId(
        @Body buplicationRequest: DuplicationIdRequest
    ) : ResponseState<ApiResponse<Nothing>>

    @POST("users/nickname/validation")
    suspend fun checkDuplicationNickname(
        @Body buplicationRequest: DuplicationNicknameRequest
    ) : ResponseState<ApiResponse<Nothing>>

    @POST("sign-up")
    suspend fun signUpAccount(
        @Body signUpRequest: SignUpRequest
    ) : ResponseState<ApiResponse<Token>>

    @POST("sign-in")
    suspend fun signInAccount(
        @Body accountRequest: AccountRequest
    ) : ResponseState<ApiResponse<JwtToken>>

    @PATCH("users/reset-password")
    suspend fun resetPasswordAccount(
        @Body accountRequest: AccountRequest
    ) : ResponseState<ApiResponse<Nothing>>

    @GET("users/findId")
    suspend fun findAccountId(
        @Query("email") email : String,
        @Query("authCode") code : String
    ) : ResponseState<ApiResponse<UserId>>

    @GET("email/verifications")
    suspend fun verificationEmailCode(
        @Query("email") email : String,
        @Query("authCode") code : String
    ) : ResponseState<ApiResponse<Nothing>>

    @GET("email/verification-request")
    suspend fun requestEmailCode(
        @Query("email") email : String
    ) : ResponseState<ApiResponse<Nothing>>

    @GET("users/findPwd")
    suspend fun verificationUserEmail(
        @Query("id") id : String,
        @Query("email") email : String,
        @Query("authCode") code : String
    ) : ResponseState<ApiResponse<Nothing>>

}