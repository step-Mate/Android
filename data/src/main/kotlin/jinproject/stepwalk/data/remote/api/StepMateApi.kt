package jinproject.stepwalk.data.remote.api

import jinproject.stepwalk.data.remote.dto.request.DuplicationRequest
import jinproject.stepwalk.data.remote.dto.request.AccountRequest
import jinproject.stepwalk.data.remote.dto.request.SignUpRequest
import jinproject.stepwalk.data.remote.dto.response.IdResponse
import jinproject.stepwalk.data.remote.dto.response.Response
import jinproject.stepwalk.data.remote.dto.response.TokenResponse
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface StepMateApi {

    @POST("users/id/validation")
    suspend fun checkDuplicationId(
        @Body buplicationRequest: DuplicationRequest
    ) : Response
    @POST("sign-up")
    suspend fun signUpAccount(
        @Body signUpRequest: SignUpRequest
    ) : TokenResponse

    @POST("sign-in")
    suspend fun signInAccount(
        @Body accountRequest: AccountRequest
    ) : TokenResponse

    @PATCH("users/reset-password")
    suspend fun resetPasswordAccount(
        @Body accountRequest: AccountRequest
    ) : Response

    @GET("users/findId")
    suspend fun findAccountId(
        @Query("email") email : String,
        @Query("authCode") code : Int
    ) : IdResponse

    @GET("email/verifications")
    suspend fun verificationEmailCode(
        @Query("email") email : String,
        @Query("authCode") code : Int
    ) : Response

    @GET("email/verification-request")
    suspend fun requestEmailCode(
        @Query("email") email : String
    ) : Response

}