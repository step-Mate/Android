package com.stepmate.domain.usecase.auth

import com.stepmate.domain.model.ResponseState
import com.stepmate.domain.repository.AuthRepository
import javax.inject.Inject

class CheckNicknameUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(nickname : String) : ResponseState<Boolean> =
        authRepository.checkDuplicationNickname(nickname)
}