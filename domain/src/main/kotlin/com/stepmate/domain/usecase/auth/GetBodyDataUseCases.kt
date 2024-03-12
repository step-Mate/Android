package com.stepmate.domain.usecase.auth

import com.stepmate.domain.model.BodyData
import com.stepmate.domain.repository.AuthRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class GetBodyDataUseCases @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): BodyData = authRepository.getBodyData().first()
}