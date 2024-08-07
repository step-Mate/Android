package com.stepmate.login.screen.state

import androidx.compose.runtime.Stable
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@Stable
data class FieldValue(
    val text: String = "",
    val valid: SignValid = SignValid.blank
)

@Stable
class Account(
    time: Long,
    initText: String = "",
    initValid: SignValid = SignValid.blank
) {
    private val _value = MutableStateFlow(FieldValue(initText, initValid))
    val value get() = _value.asStateFlow()

    @OptIn(FlowPreview::class)
    private val debouncedValueFilter: Flow<FieldValue> = value
        .debounce(time)
        .filter { it.text.isNotEmpty() }
        .distinctUntilChanged { old, new -> old.text == new.text }

    fun updateValue(value: String) {
        _value.update { it.copy(text = value) }
    }

    fun updateValid(valid: SignValid) {
        _value.update { it.copy(valid = valid) }
    }

    fun now() = value.value.text

    fun nowValid() = value.value.valid

    fun isSuccessful(): Boolean = nowValid() == SignValid.success

    fun checkValid(
        checkValid: SignValid = SignValid.notValid,
        check: (String) -> Boolean
    ) = debouncedValueFilter
        .onEach { nowField ->
            _value.update { fieldValue ->
                fieldValue.copy(
                    valid = when {
                        nowField.text.isBlank() -> SignValid.blank
                        !check(nowField.text) -> checkValid
                        else -> SignValid.success
                    }
                )
            }
        }

    suspend fun checkValid(
        check: (String) -> Boolean,
        suspendCheck: suspend (String) -> Boolean,
        suspendValid: SignValid
    ) = debouncedValueFilter
        .onEach { nowField ->
            _value.update { fieldValue ->
                fieldValue.copy(
                    valid = when {
                        nowField.text.isBlank() -> SignValid.blank
                        !check(nowField.text) -> SignValid.notValid
                        !suspendCheck(nowField.text) -> suspendValid
                        else -> SignValid.success
                    }
                )
            }
        }
}

enum class SignValid {
    blank, notValid, duplicationId, notMatch, verifying, success
}

internal fun SignValid.isError(): Boolean =
    (this != SignValid.success) and (this != SignValid.blank)

internal fun SignValid.isSuccess(): Boolean = this == SignValid.success