package com.stepmate.domain.usecase.user

import com.stepmate.domain.model.user.User
import com.stepmate.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMyInfoUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<User> = userRepository.getMyInfo()
}