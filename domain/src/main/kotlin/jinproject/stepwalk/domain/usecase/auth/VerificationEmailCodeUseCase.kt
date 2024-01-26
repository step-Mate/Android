package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.repository.AuthRepository
import javax.inject.Inject

interface VerificationEmailCodeUseCase {
    suspend operator fun invoke(email : String, code : String) : ResponseState<Boolean>
}

class VerificationEmailCodeUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : VerificationEmailCodeUseCase{
    override suspend operator fun invoke(email : String, code : String) : ResponseState<Boolean> =
        authRepository.verificationEmailCode(email, code)
}