package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.repository.AuthRepository
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(id : String, password : String) : ResponseState<String> =
        authRepository.signInAccount(id, password)
}