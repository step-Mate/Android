package com.stepmate.domain.usecase.mission

import com.stepmate.domain.model.mission.MissionType
import com.stepmate.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMissionAchievedUseCases @Inject constructor(
    private val missionRepository: MissionRepository
) {
    operator fun invoke(missionType: MissionType) : Flow<Int> = missionRepository.getMissionAchieved(missionType)
}