package jinproject.stepwalk.data

import jinproject.stepwalk.data.remote.api.StepMateApi
import jinproject.stepwalk.data.remote.dto.request.AccountRequest
import jinproject.stepwalk.data.remote.dto.request.DuplicationRequest
import jinproject.stepwalk.data.remote.mapper.toSignUpRequest
import jinproject.stepwalk.data.remote.utils.checkApiException
import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.model.UserData
import jinproject.stepwalk.domain.model.transResponseState
import jinproject.stepwalk.domain.repository.AuthRepository
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val stepMateApi: StepMateApi
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

    override suspend fun signUpAccount(userData: UserData): ResponseState<String> =
        checkApiException {
            val response = stepMateApi.signUpAccount(userData.toSignUpRequest())
            transResponseState(
                code = response.code,
                message = response.message,
                result = response.result.token
            )
        }

    override suspend fun signInAccount(id: String, password: String): ResponseState<String> =
        checkApiException {
            val response = stepMateApi.signInAccount(AccountRequest(id,password))
            transResponseState(
                code = response.code,
                message = response.message,
                result = response.result.token
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
}