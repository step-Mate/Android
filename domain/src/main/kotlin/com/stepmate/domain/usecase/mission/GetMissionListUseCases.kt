package com.stepmate.domain.usecase.mission

import com.stepmate.domain.model.mission.MissionList
import com.stepmate.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetMissionListUseCases @Inject constructor(
    private val missionRepository: MissionRepository
) {
    operator fun invoke(title: String): Flow<MissionList> =
        missionRepository.getMissionList(title).map { mission ->
            MissionList(mission.title,
                mission.list.sortedBy { it.getMissionGoal() }
            )
        }
}