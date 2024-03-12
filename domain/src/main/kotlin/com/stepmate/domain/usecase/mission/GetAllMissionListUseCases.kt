package com.stepmate.domain.usecase.mission

import com.stepmate.domain.model.mission.MissionComposite
import com.stepmate.domain.model.mission.MissionList
import com.stepmate.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class GetAllMissionListUseCases @Inject constructor(
    private val missionRepository: MissionRepository
) {
    operator fun invoke(): Flow<List<MissionList>> = missionRepository.getAllMissionList().map {list ->
        list.map { mission ->
            MissionList(mission.title,
                if (mission.list.first() is MissionComposite)
                    mission.list.sortedBy { (it as MissionComposite).getOriginalGoal() }
                else
                    mission.list.sortedBy { it.getMissionGoal() }
            )
        }
    }
}