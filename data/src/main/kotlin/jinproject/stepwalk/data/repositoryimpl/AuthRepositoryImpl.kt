package jinproject.stepwalk.data.repositoryimpl

import jinproject.stepwalk.data.local.datasource.BodyDataSource
import jinproject.stepwalk.data.local.datasource.CurrentAuthDataSource
import jinproject.stepwalk.data.remote.api.StepMateApi
import jinproject.stepwalk.data.remote.dto.request.AccountRequest
import jinproject.stepwalk.data.remote.dto.request.DuplicationIdRequest
import jinproject.stepwalk.data.remote.dto.request.DuplicationNicknameRequest
import jinproject.stepwalk.data.remote.mapper.toSignUpRequest
import jinproject.stepwalk.data.remote.utils.getResult
import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.model.SignUpData
import jinproject.stepwalk.domain.model.onSuccess
import jinproject.stepwalk.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val stepMateApi: StepMateApi,
    private val currentAuthDataSource: CurrentAuthDataSource,
    private val bodyDataSource: BodyDataSource,
) : AuthRepository {
    override suspend fun checkDuplicationId(id: String): ResponseState<Boolean> =
        stepMateApi.checkDuplicationId(DuplicationIdRequest(id)).getResult()


    override suspend fun checkDuplicationNickname(nickname: String): ResponseState<Boolean> =
        stepMateApi.checkDuplicationNickname(
            DuplicationNicknameRequest(
                nickname
            )
        ).getResult()


    override suspend fun signUpAccount(signUpData: SignUpData): Flow<ResponseState<Boolean>> =
        flow {
            emit(ResponseState.Loading)
            val body = bodyDataSource.getBodyData().first()
            val response = stepMateApi.signUpAccount(
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
            val response = stepMateApi.signInAccount(AccountRequest(id, password))
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
        password: String
    ): ResponseState<Boolean> =
        stepMateApi.resetPasswordAccount(AccountRequest(id, password))
            .getResult()


    override suspend fun findAccountId(email: String, code: String): Flow<ResponseState<String>> =
        flow {
            emit(ResponseState.Loading)
            emit(
                stepMateApi.findAccountId(email, code)
                    .getResult { findId -> ResponseState.Result(findId?.userId!!)})
        }

    override suspend fun verificationEmailCode(
        email: String,
        code: String
    ): ResponseState<Boolean> =
        stepMateApi.verificationEmailCode(email, code).getResult()


    override suspend fun requestEmailCode(email: String): ResponseState<Boolean> =
        stepMateApi.requestEmailCode(email).getResult()


    override suspend fun verificationUserEmail(
        id: String,
        email: String,
        code: String
    ): Flow<ResponseState<Boolean>> =
        flow {
            emit(ResponseState.Loading)
            emit(stepMateApi.verificationUserEmail(id, email, code).getResult())
        }

    override suspend fun logoutAccount() = currentAuthDataSource.clearAuth()

}