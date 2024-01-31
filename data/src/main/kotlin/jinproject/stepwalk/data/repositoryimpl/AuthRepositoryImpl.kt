package jinproject.stepwalk.data.repositoryimpl

import jinproject.stepwalk.data.local.datasource.CurrentAuthDataSource
import jinproject.stepwalk.data.remote.api.StepMateApi
import jinproject.stepwalk.data.remote.dto.request.AccountRequest
import jinproject.stepwalk.data.remote.dto.request.DuplicationIdRequest
import jinproject.stepwalk.data.remote.dto.request.DuplicationNicknameRequest
import jinproject.stepwalk.data.remote.mapper.toSignUpRequest
import jinproject.stepwalk.data.remote.utils.exchangeResultFlow
import jinproject.stepwalk.data.remote.utils.getResult
import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.model.UserData
import jinproject.stepwalk.domain.model.onSuccess
import jinproject.stepwalk.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val stepMateApi: StepMateApi,
    private val currentAuthDataSource: CurrentAuthDataSource
) : AuthRepository {
    override suspend fun checkDuplicationId(id: String): ResponseState<Boolean> =
        withContext(Dispatchers.IO) {
            return@withContext stepMateApi.checkDuplicationId(DuplicationIdRequest(id)).getResult()
        }

    override suspend fun checkDuplicationNickname(nickname: String): ResponseState<Boolean> =
        withContext(Dispatchers.IO) {
            return@withContext stepMateApi.checkDuplicationNickname(DuplicationNicknameRequest(nickname)).getResult()
        }

    override suspend fun signUpAccount(userData: UserData): Flow<ResponseState<Boolean>> =
        exchangeResultFlow {
            val body = async { currentAuthDataSource.getBodyData().first()}
            val response = async {
                stepMateApi.signUpAccount(
                    userData.copy(
                        age = body.await().age,
                        height = body.await().height,
                        weight = body.await().weight
                    ).toSignUpRequest()
                )
            }
            response.await().onSuccess {
                currentAuthDataSource.setAuthData(userData.nickname,it?.result!!.token)
            }
            return@exchangeResultFlow response.await().getResult { ResponseState.Result(true) }
        }

    override suspend fun signInAccount(id: String, password: String): Flow<ResponseState<Boolean>> =
        exchangeResultFlow {
            val response = async { stepMateApi.signInAccount(AccountRequest(id,password))}
            response.await().onSuccess {
                currentAuthDataSource.setToken(accessToken = it?.result!!.token, refreshToken = "")//추후 api수정시 수정
            }
            return@exchangeResultFlow response.await().getResult { ResponseState.Result(true) }
        }

    override suspend fun resetPasswordAccount(id: String, password: String): ResponseState<Boolean> =
        withContext(Dispatchers.IO) {
            return@withContext stepMateApi.resetPasswordAccount(AccountRequest(id, password)).getResult()
        }

    override suspend fun findAccountId(email: String, code: String): Flow<ResponseState<String>> =
        exchangeResultFlow {
            return@exchangeResultFlow stepMateApi.findAccountId(email, code).getResult{
                ResponseState.Result(it?.id)
            }
        }

    override suspend fun verificationEmailCode(email: String, code: String): ResponseState<Boolean> =
        withContext(Dispatchers.IO) {
            return@withContext stepMateApi.verificationEmailCode(email, code).getResult()
        }

    override suspend fun requestEmailCode(email: String): ResponseState<Boolean> =
        withContext(Dispatchers.IO) {
            return@withContext stepMateApi.requestEmailCode(email).getResult()
        }

    override suspend fun verificationUserEmail(id: String, email: String, code: String) : Flow<ResponseState<Boolean>> =
        exchangeResultFlow {
            return@exchangeResultFlow stepMateApi.verificationUserEmail(id, email, code).getResult()
        }

    override suspend fun logoutAccount() {
        withContext(Dispatchers.IO){
            currentAuthDataSource.clearAuth()
        }
    }
}