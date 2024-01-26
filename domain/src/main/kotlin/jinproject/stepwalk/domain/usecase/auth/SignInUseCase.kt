package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.repository.AuthRepository
import javax.inject.Inject

interface SignInUseCase {
    suspend operator fun invoke(id : String, password : String, isAutoLogin : Boolean) : ResponseState<Boolean>
}

class SignInUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : SignInUseCase {
    override suspend operator fun invoke(id : String, password : String, isAutoLogin : Boolean) : ResponseState<Boolean> =
        authRepository.signInAccount(id, password,isAutoLogin)
}