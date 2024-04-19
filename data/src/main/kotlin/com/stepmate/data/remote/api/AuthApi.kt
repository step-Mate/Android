package com.stepmate.data.remote.api

import com.stepmate.data.remote.dto.request.AccountRequest
import com.stepmate.data.remote.dto.request.DuplicationIdRequest
import com.stepmate.data.remote.dto.request.DuplicationNicknameRequest
import com.stepmate.data.remote.dto.request.SignUpRequest
import com.stepmate.data.remote.dto.response.AccessToken
import com.stepmate.data.remote.dto.response.ApiResponse
import com.stepmate.data.remote.dto.response.Token
import com.stepmate.data.remote.dto.response.UserId
import com.stepmate.domain.model.ResponseState
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query

interface AuthApi {

    @POST("users/id/validation")
    suspend fun checkDuplicationId(
        @Body duplicationRequest: DuplicationIdRequest
    ): ResponseState<ApiResponse<Nothing>>

    @POST("users/nickname/validation")
    suspend fun checkDuplicationNickname(
        @Body duplicationRequest: DuplicationNicknameRequest
    ): ResponseState<ApiResponse<Nothing>>

    @POST("sign-up")
    suspend fun signUpAccount(
        @Body signUpRequest: SignUpRequest
    ): ResponseState<ApiResponse<Token>>

    @POST("sign-in")
    suspend fun signInAccount(
        @Body accountRequest: AccountRequest
    ): ResponseState<ApiResponse<Token>>

    @PATCH("users/reset-password")
    suspend fun resetPasswordAccount(
        @Body accountRequest: AccountRequest
    ): ResponseState<ApiResponse<Nothing>>

    @GET("users/findId")
    suspend fun findAccountId(
        @Query("email") email: String,
        @Query("authCode") code: String
    ): ResponseState<ApiResponse<UserId>>

    @GET("email/verifications")
    suspend fun verificationEmailCode(
        @Query("email") email: String,
        @Query("authCode") code: String
    ): ResponseState<ApiResponse<Nothing>>

    @GET("email/verification-request")
    suspend fun requestEmailCode(
        @Query("email") email: String
    ): ResponseState<ApiResponse<Nothing>>

    @GET("users/findPwd")
    suspend fun verificationUserEmail(
        @Query("userId") id: String,
        @Query("email") email: String,
        @Query("authCode") code: String
    ): ResponseState<ApiResponse<Nothing>>

    @GET("reissue")
    suspend fun reIssueAccessToken(
        @Query("Authorization") refreshToken: String
    ): ResponseState<ApiResponse<AccessToken>>

}