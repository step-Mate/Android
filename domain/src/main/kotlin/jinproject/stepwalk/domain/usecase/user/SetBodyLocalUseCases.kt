package jinproject.stepwalk.domain.usecase.user

import jinproject.stepwalk.domain.model.BodyData
import jinproject.stepwalk.domain.repository.UserRepository
import javax.inject.Inject

class SetBodyLocalUseCases @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(bodyData: BodyData) = userRepository.setBodyLocalData(bodyData)
    suspend fun setAge(age: Int) = userRepository.setBodyAge(age)
    suspend fun setWeight(weight: Int) = userRepository.setBodyWeight(weight)
    suspend fun setHeight(height: Int) = userRepository.setBodyHeight(height)
}