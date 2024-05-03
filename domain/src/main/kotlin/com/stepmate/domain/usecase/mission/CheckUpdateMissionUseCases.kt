package com.stepmate.domain.usecase.mission

import com.stepmate.domain.repository.MissionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckUpdateMissionUseCases @Inject constructor(
    private val missionRepository: MissionRepository
) {
    suspend operator fun invoke(): List<String> {
        val complete = missionRepository.checkUpdateMission(
            missionRepository.getAllLocalMissionList().first().sortedBy { it.title })
        complete.forEach { designation ->
            missionRepository.completeMission(designation)
        }
        return complete
    }
}