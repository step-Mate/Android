package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.model.BodyData
import jinproject.stepwalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBodyDataUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<BodyData> = userRepository.getBodyData()
}