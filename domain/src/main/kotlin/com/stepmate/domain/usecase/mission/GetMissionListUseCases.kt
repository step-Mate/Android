package com.stepmate.domain.usecase.mission

import com.stepmate.domain.model.mission.MissionList
import com.stepmate.domain.repository.LocalMissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMissionListUseCases @Inject constructor(
    private val localMissionRepository: LocalMissionRepository
) {
    operator fun invoke(title: String): Flow<MissionList> =
        localMissionRepository.getMissionList(title).map { mission ->
            MissionList(mission.title,
                mission.list.sortedBy { it.getMissionGoal() }
            )
        }
}