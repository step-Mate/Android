package com.stepmate.domain.usecase.user

import com.stepmate.domain.model.BodyData
import com.stepmate.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBodyDataUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    operator fun invoke(): Flow<BodyData> = userRepository.getBodyData()
}