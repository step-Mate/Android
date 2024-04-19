package com.stepmate.domain.usecase.mission

import com.stepmate.domain.repository.MissionRepository
import javax.inject.Inject

class ResetMissionTimeUseCases @Inject constructor(
    private val missionRepository: MissionRepository
) {
    suspend operator fun invoke() = missionRepository.resetMissionTime()
}