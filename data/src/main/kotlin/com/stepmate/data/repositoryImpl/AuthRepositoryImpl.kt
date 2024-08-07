package com.stepmate.data.repositoryImpl

import com.stepmate.data.local.database.dao.MissionLocal
import com.stepmate.data.local.datasource.BodyDataSource
import com.stepmate.data.local.datasource.CurrentAuthDataSource
import com.stepmate.data.remote.api.AuthApi
import com.stepmate.data.remote.dto.request.AccountRequest
import com.stepmate.data.remote.dto.request.DuplicationIdRequest
import com.stepmate.data.remote.dto.request.DuplicationNicknameRequest
import com.stepmate.data.remote.mapper.toSignUpRequest
import com.stepmate.data.remote.utils.getResult
import com.stepmate.domain.model.BodyData
import com.stepmate.domain.model.ResponseState
import com.stepmate.domain.model.SignUpData
import com.stepmate.domain.model.onSuccess
import com.stepmate.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthApi,
    private val missionLocal : MissionLocal,
    private val currentAuthDataSource: CurrentAuthDataSource,
    private val bodyDataSource: BodyDataSource,
) : AuthRepository {
    override suspend fun checkDuplicationId(id: String): ResponseState<Boolean> =
        authApi.checkDuplicationId(DuplicationIdRequest(id)).getResult()


    override suspend fun checkDuplicationNickname(nickname: String): ResponseState<Boolean> =
        authApi.checkDuplicationNickname(
            DuplicationNicknameRequest(
                nickname
            )
        ).getResult()


    override suspend fun signUpAccount(signUpData: SignUpData): Flow<ResponseState<Boolean>> =
        flow {
            emit(ResponseState.Loading)
            val body = bodyDataSource.getBodyData().first()
            val response = authApi.signUpAccount(
                signUpData.copy(
                    age = body.age,
                    height = body.height,
                    weight = body.weight
                ).toSignUpRequest()
            )
            response.onSuccess { token ->
                currentAuthDataSource.setToken(
                    token?.result!!.accessToken,
                    token.result.refreshToken
                )
            }
            emit(response.getResult { ResponseState.Result(true) })
        }

    override suspend fun signInAccount(id: String, password: String): Flow<ResponseState<Boolean>> =
        flow {
            emit(ResponseState.Loading)
            val response = authApi.signInAccount(AccountRequest(id, password))
            response.onSuccess { token ->
                currentAuthDataSource.setToken(
                    accessToken = token?.result!!.accessToken,
                    refreshToken = token.result.refreshToken
                )
            }
            emit(response.getResult { ResponseState.Result(true) })
        }

    override suspend fun resetPasswordAccount(
        id: String,
        password: String,
    ): ResponseState<Boolean> =
        authApi.resetPasswordAccount(AccountRequest(id, password))
            .getResult()


    override suspend fun findAccountId(email: String, code: String): Flow<ResponseState<String>> =
        flow {
            emit(ResponseState.Loading)
            emit(
                authApi.findAccountId(email, code)
                    .getResult { findId -> ResponseState.Result(findId?.userId!!) })
        }

    override suspend fun verificationEmailCode(
        email: String,
        code: String,
    ): ResponseState<Boolean> =
        authApi.verificationEmailCode(email, code).getResult()


    override suspend fun requestEmailCode(email: String): ResponseState<Boolean> =
        authApi.requestEmailCode(email).getResult()


    override suspend fun verificationUserEmail(
        id: String,
        email: String,
        code: String,
    ): Flow<ResponseState<Boolean>> =
        flow {
            emit(ResponseState.Loading)
            emit(authApi.verificationUserEmail(id, email, code).getResult())
        }

    override suspend fun logoutAccount() {
        currentAuthDataSource.clearAuth()
        missionLocal.deleteMission()
    }

    override fun getBodyData(): Flow<BodyData> =
        bodyDataSource.getBodyData()

    override suspend fun setBodyData(bodyData: BodyData) =
        bodyDataSource.setBodyData(bodyData)

    override fun getAccessToken(): Flow<String> = currentAuthDataSource.getAccessToken()
}