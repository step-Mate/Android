package com.stepmate.domain.usecase.user

import com.stepmate.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFriendRequestUseCase @Inject constructor(private val userRepository: UserRepository) {
    operator fun invoke(): Flow<List<String>> = userRepository.getFriendRequest()
}