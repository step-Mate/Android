package com.stepmate.domain.usecase.mission

import com.stepmate.domain.repository.MissionRepository
import javax.inject.Inject

class UpdateMissionUseCases @Inject constructor(
    private val missionRepository: MissionRepository
) {
    suspend operator fun invoke(achieved: Int) = missionRepository.updateMission(achieved)
}