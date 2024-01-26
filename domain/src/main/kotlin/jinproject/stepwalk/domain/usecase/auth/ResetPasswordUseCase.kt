package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.repository.AuthRepository
import javax.inject.Inject

interface ResetPasswordUseCase {
    suspend operator fun invoke(id : String, password : String) : ResponseState<Boolean>
}

class ResetPasswordUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : ResetPasswordUseCase {
    override suspend operator fun invoke(id : String, password : String) : ResponseState<Boolean> =
        authRepository.resetPasswordAccount(id, password)
}