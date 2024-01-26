package jinproject.stepwalk.domain.usecase.auth

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

interface FindIdUseCase {
    suspend operator fun invoke(email : String, code : String) : Flow<ResponseState<String>>
}

class FindIdUseCaseImpl @Inject constructor(
    private val authRepository: AuthRepository
) : FindIdUseCase {
    override suspend operator fun invoke(email : String, code : String) : Flow<ResponseState<String>> =
        authRepository.findAccountId(email, code)
}