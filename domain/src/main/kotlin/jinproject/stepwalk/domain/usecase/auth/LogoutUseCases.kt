package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCases @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.logoutAccount()
}