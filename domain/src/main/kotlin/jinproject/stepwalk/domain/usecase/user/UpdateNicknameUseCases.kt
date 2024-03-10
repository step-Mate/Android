package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.repository.UserRepository
import javax.inject.Inject

class UpdateNicknameUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(nickname: String) = userRepository.updateNickname(nickname)
}