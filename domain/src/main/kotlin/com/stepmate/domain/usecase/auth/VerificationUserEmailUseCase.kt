package com.stepmate.domain.usecase.auth

import com.stepmate.domain.model.ResponseState
import com.stepmate.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class VerificationUserEmailUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(id : String, email : String, code : String) : Flow<ResponseState<Boolean>> =
        authRepository.verificationUserEmail(id, email, code)
}