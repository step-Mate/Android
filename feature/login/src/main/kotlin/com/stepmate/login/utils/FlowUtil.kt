package com.stepmate.login.utils

import com.stepmate.domain.model.ResponseState
import com.stepmate.domain.model.onException
import com.stepmate.domain.model.onLoading
import com.stepmate.domain.model.onSuccess
import com.stepmate.login.screen.state.AuthState
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