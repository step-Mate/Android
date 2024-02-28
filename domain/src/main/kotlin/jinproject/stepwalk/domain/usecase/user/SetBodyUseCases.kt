package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.model.BodyData
import jinproject.stepwalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetBodyUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(bodyData: BodyData): Flow<Boolean> = userRepository.setBodyData(bodyData)
}