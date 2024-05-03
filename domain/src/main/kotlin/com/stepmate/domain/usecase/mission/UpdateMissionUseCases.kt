package com.stepmate.domain.usecase.mission

import com.stepmate.domain.repository.LocalMissionRepository
import com.stepmate.domain.repository.UserRepository
import javax.inject.Inject

class UpdateMissionUseCases @Inject constructor(
    private val localMissionRepository: LocalMissionRepository,
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(achieved: Int) =
        localMissionRepository.updateMission(
            step = achieved,
            calories = userRepository.getCalories(achieved).toInt()
        )
}