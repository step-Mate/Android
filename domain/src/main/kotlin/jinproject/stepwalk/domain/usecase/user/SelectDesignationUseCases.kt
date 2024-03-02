package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SelectDesignationUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(designation: String): Flow<Boolean> =
        userRepository.selectDesignation(designation)
}