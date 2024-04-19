package com.stepmate.domain.usecase.rank

import com.stepmate.domain.model.rank.UserStepRank
import com.stepmate.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetUserRankUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<UserStepRank> = userRepository.getMyRank()
}