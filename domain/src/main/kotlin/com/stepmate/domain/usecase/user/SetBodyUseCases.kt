package com.stepmate.domain.usecase.user

import com.stepmate.domain.model.BodyData
import com.stepmate.domain.repository.UserRepository
import javax.inject.Inject

class SetBodyUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(bodyData: BodyData) = userRepository.setBodyData(bodyData)
}