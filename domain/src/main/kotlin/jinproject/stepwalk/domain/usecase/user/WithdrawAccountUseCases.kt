package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class WithdrawAccountUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(password: String) : Flow<Boolean> = userRepository.withdrawAccount(password)
}