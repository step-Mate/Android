package com.stepmate.domain.usecase.mission

import com.stepmate.domain.model.mission.MissionList
import com.stepmate.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetAllMissionListUseCases @Inject constructor(
    private val missionRepository: MissionRepository
) {
    operator fun invoke(): Flow<List<MissionList>> =
        missionRepository.getAllLocalMissionList()
}