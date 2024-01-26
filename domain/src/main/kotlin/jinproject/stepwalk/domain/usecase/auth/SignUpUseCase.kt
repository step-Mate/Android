package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.model.UserData
import jinproject.stepwalk.domain.repository.AuthRepository
import javax.inject.Inject

interface SignUpUseCase {
    suspend operator fun invoke(userData: UserData) : ResponseState<Boolean>
}

class SignUpUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : SignUpUseCase{
    override suspend operator fun invoke(userData: UserData) : ResponseState<Boolean> = authRepository.signUpAccount(userData)
}