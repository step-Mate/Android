package com.stepmate.domain.usecase.mission

import com.stepmate.domain.repository.MissionRepository
import javax.inject.Inject

class UpdateMissionListUseCases @Inject constructor(
    private val missionRepository: MissionRepository
) {
    suspend operator fun invoke() =
        missionRepository.updateMissionList(missionRepository.getLocalMissionList())
}