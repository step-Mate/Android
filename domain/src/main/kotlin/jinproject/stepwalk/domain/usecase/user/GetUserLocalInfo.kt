package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.repository.UserRepository
import javax.inject.Inject

class GetUserLocalInfo @Inject constructor(
    private val userRepository: UserRepository
){
    operator fun invoke() = userRepository.getUserLocalInfo()
}