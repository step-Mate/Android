package jinproject.stepwalk.login.screen.state

import androidx.compose.runtime.Stable
import jinproject.stepwalk.login.utils.debouncedFilter
import jinproject.stepwalk.login.utils.isValidEmailCode
import jinproject.stepwalk.login.utils.isValidID
import jinproject.stepwalk.login.utils.passwordMatches
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

@Stable
data class FieldValue(
    val text: String = "",
    val valid : SignValid = SignValid.blank
)

@Stable
class Account(
    time : Long
){
    private val _value = MutableStateFlow(FieldValue())
    val value get() = _value.asStateFlow()

    private val debouncedValueFilter : Flow<FieldValue?> = value
        .debouncedFilter(time)

    fun updateValue(value : String){
        _value.update { it.copy(text = value) }
    }

    fun updateValid(valid: SignValid){
        _value.update { it.copy(valid = valid) }
    }

    fun now() = value.value.text

    fun nowValid() = value.value.valid

    fun isSuccessful() : Boolean = nowValid() == SignValid.success

    fun checkValid(check : (String) -> Boolean) = debouncedValueFilter
        .onEach {
            it?.let {
                _value.update {fieldValue ->
                    fieldValue.copy(valid = when {
                        it.text.isBlank() -> SignValid.blank
                        !check(it.text) -> SignValid.notValid
                        else -> SignValid.success
                    })
                }
            }
        }

    suspend fun checkEmailCodeValid(check : suspend (String) -> Boolean) = debouncedValueFilter
        .onEach {
            it?.let {
                _value.update {fieldValue ->
                    fieldValue.copy(valid = when {
                        it.text.isBlank() -> SignValid.blank
                        it.text.isValidEmailCode() -> SignValid.notValid
                        !check(it.text) -> SignValid.notValid
                        else -> SignValid.success
                    })
                }
            }
        }

    suspend fun checkIdValid(checkId : suspend (String) -> Boolean) = debouncedValueFilter
        .onEach {
            it?.let {
                _value.update {fieldValue ->
                    fieldValue.copy(valid = when {
                        it.text.isBlank() -> SignValid.blank
                        !it.text.isValidID() -> SignValid.notValid
                        !checkId(it.text) -> SignValid.notValid
                        else -> SignValid.success
                    })
                }
            }
        }

    fun checkRepeatPasswordValid(password : StateFlow<String>) = debouncedValueFilter
        .onEach {
            it?.let {
                _value.update {fieldValue ->
                    fieldValue.copy(valid = when {
                        it.text.isBlank() -> SignValid.blank
                        !it.text.passwordMatches(password.value) -> SignValid.notMatch
                        else -> SignValid.success
                    })
                }
            }
        }
}

enum class SignValid {
    blank,notValid,duplicationId,notMatch,verifying,success
}

internal fun SignValid.isError() : Boolean = (this != SignValid.success) and (this != SignValid.blank)