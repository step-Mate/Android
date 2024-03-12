package com.stepmate.domain.usecase.auth

import com.stepmate.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCases @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke() = authRepository.logoutAccount()
}