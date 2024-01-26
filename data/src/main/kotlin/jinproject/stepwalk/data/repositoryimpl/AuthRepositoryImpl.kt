package jinproject.stepwalk.data.repositoryimpl

import jinproject.stepwalk.data.local.datasource.CurrentAuthDataSource
import jinproject.stepwalk.data.local.datasource.UserDataSource
import jinproject.stepwalk.data.remote.api.StepMateApi
import jinproject.stepwalk.data.remote.dto.request.AccountRequest
import jinproject.stepwalk.data.remote.dto.request.DuplicationRequest
import jinproject.stepwalk.data.remote.mapper.toSignUpRequest
import jinproject.stepwalk.data.remote.utils.checkApiException
import jinproject.stepwalk.domain.model.CurrentAuth
import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.model.UserData
import jinproject.stepwalk.domain.model.transResponseState
import jinproject.stepwalk.domain.repository.AuthRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val stepMateApi: StepMateApi,
    private val userDataSource: UserDataSource,
    private val currentAuthDataSource: CurrentAuthDataSource
) : AuthRepository {
    override suspend fun checkDuplicationId(id: String): ResponseState<Boolean> =
        checkApiException {
            val response = stepMateApi.checkDuplicationId(DuplicationRequest(id))
            transResponseState(
                code = response.code,
                message = response.message,
                result = true
            )
        }

    override suspend fun signUpAccount(userData: UserData): ResponseState<Boolean> =
        checkApiException {
            val response = stepMateApi.signUpAccount(userData.toSignUpRequest())
            if (response.code == 200){
                userDataSource.setUserData(userData,response.result.token)//refresh token + 유저정보 저장
            }
            transResponseState(
                code = response.code,
                message = response.message,
                result = true
            )
        }

    override suspend fun signInAccount(id: String, password: String, isAutoLogin : Boolean): ResponseState<Boolean> =
        checkApiException {
            val response = stepMateApi.signInAccount(AccountRequest(id,password))
            if (response.code == 200){
                if (isAutoLogin){
                    currentAuthDataSource.setCurrentAuth(CurrentAuth(id,response.result.token))
                }else{
                    currentAuthDataSource.setAccessToken(response.result.token)
                }
            }
            transResponseState(
                code = response.code,
                message = response.message,
                result = true
            )
        }

    override suspend fun resetPasswordAccount(id: String, password: String): ResponseState<Boolean> =
        checkApiException {
            val response = stepMateApi.resetPasswordAccount(AccountRequest(id, password))
            transResponseState(
                code = response.code,
                message = response.message,
                result = true
            )
        }

    override suspend fun findAccountId(email: String, code: String): ResponseState<String> =
        checkApiException {
            val response = stepMateApi.findAccountId(email, code)
            transResponseState(
                code = response.code,
                message = response.message,
                result = response.result.id
            )
        }

    override suspend fun verificationEmailCode(email: String, code: String): ResponseState<Boolean> =
        checkApiException {
            val response = stepMateApi.verificationEmailCode(email, code)
            transResponseState(
                code = response.code,
                message = response.message,
                result = true
            )
        }

    override suspend fun requestEmailCode(email: String): ResponseState<Boolean> =
        checkApiException {
            val response = stepMateApi.requestEmailCode(email)
            transResponseState(
                code = response.code,
                message = response.message,
                result = true
            )
        }

    override suspend fun verificationUserEmail(id: String, email: String, code: String) : ResponseState<Boolean> =
        checkApiException {
            val response = stepMateApi.verificationUserEmail(id, email, code)
            transResponseState(
                code = response.code,
                message = response.message,
                result = true
            )
        }

    override suspend fun logoutAccount() {
        withContext(Dispatchers.IO){
            currentAuthDataSource.clearAuth()
        }
    }
}