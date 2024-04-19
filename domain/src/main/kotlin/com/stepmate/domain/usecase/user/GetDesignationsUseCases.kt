package com.stepmate.domain.usecase.user

import com.stepmate.domain.model.DesignationState
import com.stepmate.domain.repository.MissionRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetDesignationsUseCases @Inject constructor(
    private val missionRepository: MissionRepository
) {
    operator fun invoke(): Flow<DesignationState> = missionRepository.getDesignation()
}