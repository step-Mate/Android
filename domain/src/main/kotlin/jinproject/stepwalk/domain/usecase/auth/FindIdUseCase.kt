package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.repository.AuthRepository
import javax.inject.Inject

class FindIdUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email : String, code : String) : ResponseState<String> =
        authRepository.findAccountId(email, code)
}