package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class CheckHasTokenUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<Boolean> = authRepository.getAccessToken().map { token -> token.isNotBlank() }
}