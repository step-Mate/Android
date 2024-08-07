package com.stepmate.domain.usecase.user

import com.stepmate.domain.repository.UserRepository
import javax.inject.Inject

class ProcessFriendRequestUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(bool: Boolean, userName: String) = userRepository.processFriendRequest(bool, userName)
}