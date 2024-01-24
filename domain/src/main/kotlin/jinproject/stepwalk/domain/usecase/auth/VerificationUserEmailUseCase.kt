package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.repository.AuthRepository
import javax.inject.Inject

class VerificationUserEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(id : String, email : String, code : String) : ResponseState<Boolean> =
        authRepository.verificationUserEmail(id, email, code)
}