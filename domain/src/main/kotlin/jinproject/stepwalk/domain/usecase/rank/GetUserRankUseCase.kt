package jinproject.stepwalk.domain.usecase.rank

import jinproject.stepwalk.domain.model.rank.UserStepRank
import jinproject.stepwalk.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserRankUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<UserStepRank> = userRepository.getMyRank()
}