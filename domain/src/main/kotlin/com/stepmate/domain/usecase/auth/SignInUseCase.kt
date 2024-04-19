package com.stepmate.domain.usecase.auth

import com.stepmate.domain.model.ResponseState
import com.stepmate.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignInUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(id : String, password : String) : Flow<ResponseState<Boolean>> =
        authRepository.signInAccount(id, password)
}