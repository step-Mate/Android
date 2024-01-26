package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.repository.AuthRepository
import javax.inject.Inject

interface RequestEmailCodeUseCase {
    suspend operator fun invoke(email : String) : ResponseState<Boolean>
}

class RequestEmailCodeUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : RequestEmailCodeUseCase {
    override suspend operator fun invoke(email : String) : ResponseState<Boolean> =
        authRepository.requestEmailCode(email)
}