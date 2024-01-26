package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.repository.AuthRepository
import javax.inject.Inject

interface CheckIdUseCase {
    suspend operator fun invoke(id: String) : ResponseState<Boolean>
}

class CheckIdUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : CheckIdUseCase {
    override suspend operator fun invoke(id : String) : ResponseState<Boolean> = authRepository.checkDuplicationId(id)
}