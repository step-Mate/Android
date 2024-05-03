package com.stepmate.domain.usecase.mission

import com.stepmate.domain.model.mission.MissionList
import com.stepmate.domain.repository.LocalMissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllMissionListUseCases @Inject constructor(
    private val localMissionRepository: LocalMissionRepository
) {
    operator fun invoke(): Flow<List<MissionList>> =
        localMissionRepository.getAllMissionList().map { list ->
            list.map { mission ->
                MissionList(mission.title,
                    mission.list.sortedBy { it.getMissionGoal() }
                )
            }
        }
}