package com.stepmate.domain.usecase.user

import com.stepmate.domain.repository.UserRepository
import javax.inject.Inject

class UpdateNicknameUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(nickname: String) = userRepository.updateNickname(nickname)
}