package com.stepmate.domain.usecase.step

import com.stepmate.domain.repository.UserRepository
import javax.inject.Inject

class SetUserDayStepUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend fun addStep(step: Int) = userRepository.addStep(step = step)

    suspend fun queryDailyStep(step: Int) = userRepository.queryDailyStep(step = step)
}