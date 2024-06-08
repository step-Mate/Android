package com.stepmate.domain.usecase.mission

import com.stepmate.domain.model.mission.MissionCommon
import com.stepmate.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMissionListUseCases @Inject constructor(
    private val missionRepository: MissionRepository
) {
    operator fun invoke(title: String): Flow<List<MissionCommon>> =
        missionRepository.getLocalMissionList(title)
}