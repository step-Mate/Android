package jinproject.stepwalk.login.screen.state

import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import jinproject.stepwalk.login.utils.debouncedFilter
import jinproject.stepwalk.login.utils.isValidEmailCode
import jinproject.stepwalk.login.utils.isValidID
import jinproject.stepwalk.login.utils.isValidPassword
import jinproject.stepwalk.login.utils.passwordMatches
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach

@Stable
class Account(
    time : Long
){
    private val _value = MutableStateFlow("")
    val value = _value.asStateFlow()
    var valid by mutableStateOf(SignValid.blank)

    private val debouncedValueFilter : Flow<String?> = value
        .debouncedFilter(time)

    fun updateValue(value : String){
        _value.value = value
    }

    fun updateValid(valid: SignValid){
        this.valid = valid
    }

    fun now() = value.value

    fun isSuccessful() : Boolean = valid == SignValid.success

    fun checkValid(check : (String) -> Boolean) = debouncedValueFilter
        .onEach {
            valid = it?.let {
                when {
                    it.isBlank() -> SignValid.blank
                    !check(it) -> SignValid.notValid
                    else -> SignValid.success
                }
            } ?: SignValid.blank
        }

    suspend fun checkEmailCodeValid(check : suspend (String) -> Boolean) = debouncedValueFilter
        .onEach {
            valid = it?.let {
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
            valid = it?.let {
                when {
                    it.isBlank() -> SignValid.blank
                    !it.isValidID() -> SignValid.notValid
                    !checkId(it) -> SignValid.duplicationId
                    else -> SignValid.success
                }
            } ?: SignValid.blank
        }

    fun checkRepeatPasswordValid(password : String) = debouncedValueFilter
        .onEach {
            valid = it?.let {
                when {
                    it.isBlank() -> SignValid.blank
                    !it.isValidPassword() -> SignValid.notValid
                    !it.passwordMatches(password) -> SignValid.notMatch
                    else -> SignValid.success
                }
            } ?: SignValid.blank
        }
}

enum class SignValid {
    blank,notValid,duplicationId,notMatch,verifying,success
}

internal fun SignValid.isError() : Boolean = (this != SignValid.success) and (this != SignValid.blank)