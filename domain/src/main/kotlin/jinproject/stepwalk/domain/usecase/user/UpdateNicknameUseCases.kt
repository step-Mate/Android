package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class UpdateNicknameUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(nickname: String): Flow<Boolean> = userRepository.updateNickname(nickname)
}