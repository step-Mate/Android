package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.model.BodyData
import jinproject.stepwalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetBodyDataUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(): BodyData = userRepository.getBodyData().first()
}