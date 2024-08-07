package com.stepmate.login.screen.findid

import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.stepmate.domain.model.onException
import com.stepmate.domain.model.onLoading
import com.stepmate.domain.model.onSuccess
import com.stepmate.domain.usecase.auth.FindIdUseCase
import com.stepmate.domain.usecase.auth.RequestEmailCodeUseCase
import com.stepmate.login.screen.EmailViewModel
import com.stepmate.core.isValidEmail
import com.stepmate.core.isValidEmailCode
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface FindIdEvent {
    data object FindId : FindIdEvent
    data object RequestEmail : FindIdEvent
    data class Email(val value: String) : FindIdEvent
    data class EmailCode(val value: String) : FindIdEvent
}

@HiltViewModel
internal class FindIdViewModel @Inject constructor(
    requestEmailCodeUseCase: RequestEmailCodeUseCase,
    private val findIdUseCase: FindIdUseCase
) : EmailViewModel(requestEmailCodeUseCase) {

    private val _id = MutableStateFlow("")
    val id get() = _id.asStateFlow()

    init {
        email.checkValid { it.isValidEmail() }.launchIn(viewModelScope)
        emailCode.checkValid { it.isValidEmailCode() }.launchIn(viewModelScope)
    }

    fun onEvent(event: FindIdEvent) {
        when (event) {
            FindIdEvent.FindId -> {
                viewModelScope.launch(Dispatchers.IO) {
                    findIdUseCase(requestEmail, emailCode.now())
                        .onEach {
                            it.onSuccess { findId ->
                                _state.update { state ->
                                    state.copy(
                                        isSuccess = true,
                                        isLoading = false
                                    )
                                }
                                _id.value = findId ?: ""
                            }.onException { code, message ->
                                _state.update { state ->
                                    state.copy(
                                        errorMessage = message,
                                        isLoading = false
                                    )
                                }
                            }.onLoading {
                                _state.update { state -> state.copy(isLoading = true) }
                            }
                        }.launchIn(viewModelScope)

                }
            }

            FindIdEvent.RequestEmail -> {
                viewModelScope.launch(Dispatchers.IO) {
                    requestEmailVerification()
                }
            }

            is FindIdEvent.Email -> email.updateValue(event.value)
            is FindIdEvent.EmailCode -> emailCode.updateValue(event.value)
        }
    }
}
