package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.model.SignUpData
import jinproject.stepwalk.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SignUpUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(signUpData: SignUpData) : Flow<ResponseState<Boolean>> =
        authRepository.signUpAccount(signUpData)
}