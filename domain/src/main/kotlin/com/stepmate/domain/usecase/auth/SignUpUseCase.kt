package com.stepmate.domain.usecase.auth

import com.stepmate.domain.model.ResponseState
import com.stepmate.domain.model.SignUpData
import com.stepmate.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(signUpData: SignUpData) : Flow<ResponseState<Boolean>> =
        authRepository.signUpAccount(signUpData)
}