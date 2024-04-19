package com.stepmate.domain.usecase.auth

import com.stepmate.domain.model.BodyData
import com.stepmate.domain.repository.AuthRepository
import javax.inject.Inject

class SetBodyDataUseCases @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(bodyData: BodyData) = authRepository.setBodyData(bodyData)
}