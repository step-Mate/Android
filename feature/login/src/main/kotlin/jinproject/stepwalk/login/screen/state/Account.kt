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
class Account(
    time : Long,
    initValue: String = "",
    initValid : SignValid = SignValid.blank
){
    private val _value = MutableStateFlow(initValue)
    val value get() = _value.asStateFlow()

    private val _valid = MutableStateFlow(initValid)
    val valid get() = _valid.asStateFlow()

    private val debouncedValueFilter : Flow<String?> = value
        .debouncedFilter(time)

    fun updateValue(value : String){
        _value.update { value }
    }

    fun updateValid(valid: SignValid){
        _valid.update { valid }
    }

    fun now() = value.value

    fun nowValid() = valid.value

    fun isSuccessful() : Boolean = valid.value == SignValid.success

    fun checkValid(check : (String) -> Boolean) = debouncedValueFilter
        .onEach {
            _valid.value = it?.let {
                when {
                    it.isBlank() -> SignValid.blank
                    !check(it) -> SignValid.notValid
                    else -> SignValid.success
                }
            } ?: SignValid.blank
        }

    suspend fun checkEmailCodeValid(check : suspend (String) -> Boolean) = debouncedValueFilter
        .onEach {
            _valid.value = it?.let {
                when {
                    it.isBlank() -> SignValid.blank
                    !it.isValidEmailCode() -> SignValid.notValid
                    !check(it) -> SignValid.notValid
                    else -> SignValid.success
                }
            } ?: SignValid.blank
        }

    suspend fun checkIdValid(checkId : suspend (String) -> Boolean) = debouncedValueFilter
        .onEach {
            _valid.value = it?.let {
                when {
                    it.isBlank() -> SignValid.blank
                    !it.isValidID() -> SignValid.notValid
                    !checkId(it) -> SignValid.duplicationId
                    else -> SignValid.success
                }
            } ?: SignValid.blank
        }

    fun checkRepeatPasswordValid(password : StateFlow<String>) = debouncedValueFilter
        .onEach {
            _valid.value = it?.let {
                when {
                    it.isBlank() -> SignValid.blank
                    !it.passwordMatches(password.value) -> SignValid.notMatch
                    else -> SignValid.success
                }
            } ?: SignValid.blank
        }
}

enum class SignValid {
    blank,notValid,duplicationId,notMatch,verifying,success
}

internal fun SignValid.isError() : Boolean = (this != SignValid.success) and (this != SignValid.blank)