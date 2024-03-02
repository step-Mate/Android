package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.model.BodyData
import jinproject.stepwalk.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetBodyDataUseCases @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): BodyData = authRepository.getBodyData().first()
}