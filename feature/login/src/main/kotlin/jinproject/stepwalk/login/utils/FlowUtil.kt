package jinproject.stepwalk.login.utils

import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.model.onException
import jinproject.stepwalk.domain.model.onLoading
import jinproject.stepwalk.domain.model.onSuccess
import jinproject.stepwalk.login.screen.state.AuthState
import jinproject.stepwalk.login.screen.state.FieldValue
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@OptIn(FlowPreview::class)
fun StateFlow<FieldValue>.debouncedFilter(millis : Long) =
    this.debounce(millis)
    .filter {it.text.isNotEmpty() }
    .distinctUntilChanged()

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