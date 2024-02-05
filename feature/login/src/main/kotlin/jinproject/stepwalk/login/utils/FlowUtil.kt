package jinproject.stepwalk.login.utils

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.model.onException
import jinproject.stepwalk.domain.model.onLoading
import jinproject.stepwalk.domain.model.onSuccess
import jinproject.stepwalk.login.screen.state.AuthState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

fun Flow<ResponseState<Boolean>>.onEachState(state : MutableStateFlow<AuthState>) : Flow<ResponseState<Boolean>>
    = this.onEach {
    it.onSuccess {
        state.update { value-> value.copy(isSuccess = true, isLoading = false) }
        }
        .onException { code, message ->
            state.update { value-> value.copy(errorMessage = message, isLoading = false) }
        }
        .onLoading {
            state.update { value-> value.copy(isLoading = true) }
        }
    }