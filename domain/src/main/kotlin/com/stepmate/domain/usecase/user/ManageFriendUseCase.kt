package com.stepmate.domain.usecase.user

import com.stepmate.domain.repository.UserRepository
import javax.inject.Inject

class ManageFriendUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend fun addFriend(userName: String) = userRepository.addFriend(userName)

    suspend fun deleteFriend(userName: String) = userRepository.deleteFriend(userName)
}