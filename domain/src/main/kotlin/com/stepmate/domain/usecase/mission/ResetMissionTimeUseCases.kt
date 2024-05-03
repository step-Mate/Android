package com.stepmate.domain.usecase.mission

import com.stepmate.domain.repository.LocalMissionRepository
import javax.inject.Inject

class ResetMissionTimeUseCases @Inject constructor(
    private val localMissionRepository: LocalMissionRepository
) {
    suspend operator fun invoke() = localMissionRepository.resetMissionTime()
}