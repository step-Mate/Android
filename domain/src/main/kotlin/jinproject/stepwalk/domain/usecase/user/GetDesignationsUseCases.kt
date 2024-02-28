package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.model.DesignationState
import jinproject.stepwalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDesignationsUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<DesignationState> = userRepository.getDesignation()
}