package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.model.BodyData
import jinproject.stepwalk.domain.repository.UserRepository
import javax.inject.Inject

class SetBodyLocalUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(bodyData: BodyData)= userRepository.setBodyLocalData(bodyData)
}