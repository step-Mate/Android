package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.repository.UserRepository
import javax.inject.Inject

class SelectDesignationUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(designation: String) = userRepository.selectDesignation(designation)
}