package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
interface VerificationUserEmailUseCase {
    suspend operator fun invoke(id : String, email : String, code : String) : Flow<ResponseState<Boolean>>
}

class VerificationUserEmailUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : VerificationUserEmailUseCase {
    override suspend operator fun invoke(id : String, email : String, code : String) : Flow<ResponseState<Boolean>> =
        authRepository.verificationUserEmail(id, email, code)
}