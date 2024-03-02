package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class AddFriendUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(userName: String) = userRepository.addFriend(userName)
}