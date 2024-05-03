package com.stepmate.domain.usecase.mission

import com.stepmate.domain.repository.LocalMissionRepository
import com.stepmate.domain.repository.MissionRepository
import javax.inject.Inject

class UpdateMissionListUseCases @Inject constructor(
    private val localMissionRepository: LocalMissionRepository,
    private val missionRepository: MissionRepository
) {
    suspend operator fun invoke() =
        localMissionRepository.updateMissionList(missionRepository.getMissionList())
}