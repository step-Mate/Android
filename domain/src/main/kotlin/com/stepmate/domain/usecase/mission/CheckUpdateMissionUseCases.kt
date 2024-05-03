package com.stepmate.domain.usecase.mission

import com.stepmate.domain.repository.LocalMissionRepository
import com.stepmate.domain.repository.MissionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class CheckUpdateMissionUseCases @Inject constructor(
    private val missionRepository: MissionRepository,
    private val localMissionRepository: LocalMissionRepository
) {
    suspend operator fun invoke(): List<String> {
        val complete = missionRepository.checkUpdateMission(
            localMissionRepository.getAllMissionList().first().sortedBy { it.title })
        complete.forEach { designation ->
            missionRepository.completeMission(designation)
        }
        return complete
    }
}