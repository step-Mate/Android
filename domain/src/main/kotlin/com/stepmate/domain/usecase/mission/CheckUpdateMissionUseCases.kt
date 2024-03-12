package com.stepmate.domain.usecase.mission

import com.stepmate.domain.repository.MissionRepository
import javax.inject.Inject

class CheckUpdateMissionUseCases @Inject constructor(
    private val missionRepository: MissionRepository
){
    suspend operator fun invoke() : List<String> = missionRepository.checkUpdateMission()
}