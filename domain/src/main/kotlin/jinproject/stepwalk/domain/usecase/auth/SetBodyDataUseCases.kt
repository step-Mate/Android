package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.model.BodyData
import jinproject.stepwalk.domain.repository.AuthRepository
import javax.inject.Inject

class SetBodyDataUseCases @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(bodyData: BodyData) = authRepository.setBodyData(bodyData)
}