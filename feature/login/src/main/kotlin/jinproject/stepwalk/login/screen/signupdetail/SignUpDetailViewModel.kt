package jinproject.stepwalk.login.screen.signupdetail

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import jinproject.stepwalk.login.utils.isValidDouble
import jinproject.stepwalk.login.utils.isValidInt
import jinproject.stepwalk.login.utils.isValidNickname
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.login.screen.state.Verification
import jinproject.stepwalk.login.utils.isValidEmail
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject

@HiltViewModel
internal class SignUpDetailViewModel @Inject constructor(

) : ViewModel(){

    private val _nickname = MutableStateFlow("")
    val nickname = _nickname.asStateFlow()

    private val _age = MutableStateFlow("")
    val age = _age.asStateFlow()

    private val _height = MutableStateFlow("")
    val height = _height.asStateFlow()

    private val _weight = MutableStateFlow("")
    val weight = _weight.asStateFlow()

    private val _email = MutableStateFlow("")
    val email = _email.asStateFlow()

    private val _emailCode = MutableStateFlow("")
    val emailCode = _emailCode.asStateFlow()

    private var requestEmail = ""

    val valids = UserDataValid()

    //비었는지,유효한지,
    @OptIn(FlowPreview::class)
    private val debouncedNicknameFilter : Flow<String?> = nickname
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    @OptIn(FlowPreview::class)
    private val debouncedAgeFilter : Flow<String?> = age
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    @OptIn(FlowPreview::class)
    private val debouncedHeightFilter : Flow<String?> = height
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    @OptIn(FlowPreview::class)
    private val debouncedWeightFilter : Flow<String?> = weight
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    @OptIn(FlowPreview::class)
    private val debouncedEmailFilter : Flow<String?> = email
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    @OptIn(FlowPreview::class)
    private val debouncedEmailCodeFilter : Flow<String?> = emailCode
        .debounce(1000)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()


    init {
        checkNicknameValid()
        checkAgeValid()
        checkHeightValid()
        checkWeightValid()
        checkEmailValid()
        checkEmailCodeValid()
    }

    fun updateUserEvent(event : UserEvent, value : String){
        when (event){
            UserEvent.nickname -> _nickname.value = value
            UserEvent.age -> _age.value = value
            UserEvent.height -> _height.value = value
            UserEvent.weight -> _weight.value = value
            UserEvent.email -> _email.value = value
            UserEvent.emailCode -> _emailCode.value = value
        }
    }

    fun requestEmailVerification(){
        valids.emailValid.value = Verification.verifying
        requestEmail = email.value
        //서버에 이메일 인증코드 보내도록 요청
    }

    private fun checkNicknameValid() = debouncedNicknameFilter
        .onEach {
            valids.nicknameValid.value = it?.let {
                when {
                    it.isBlank() -> UserValid.blank
                    !it.isValidNickname() -> UserValid.notValid
                    else -> UserValid.success
                }
            } ?: UserValid.blank
        }.launchIn(viewModelScope)

    private fun checkAgeValid() = debouncedAgeFilter
        .onEach {
            valids.ageValid.value = it?.let {
                when {
                    it.isBlank() -> UserValid.blank
                    !it.isValidInt() -> UserValid.notValid
                    else -> UserValid.success
                }
            } ?: UserValid.blank
        }.launchIn(viewModelScope)

    private fun checkHeightValid() = debouncedHeightFilter
        .onEach {
            valids.heightValid.value = it?.let {
                when {
                    it.isBlank() -> UserValid.blank
                    !it.isValidDouble() -> UserValid.notValid
                    else -> UserValid.success
                }
            } ?: UserValid.blank
        }.launchIn(viewModelScope)

    private fun checkWeightValid() = debouncedWeightFilter
        .onEach {
            valids.weightValid.value = it?.let {
                when {
                    it.isBlank() -> UserValid.blank
                    !it.isValidDouble() -> UserValid.notValid
                    else -> UserValid.success
                }
            } ?: UserValid.blank
        }.launchIn(viewModelScope)

    private fun checkEmailValid() = debouncedEmailFilter
        .onEach {
            valids.emailValid.value = it?.let {
                when {
                    it.isBlank() -> Verification.nothing
                    !it.isValidEmail() -> Verification.emailError
                    else -> Verification.emailValid
                }
            } ?: Verification.nothing
        }.launchIn(viewModelScope)

    private fun checkEmailCodeValid() = debouncedEmailCodeFilter
        .onEach {
            valids.emailValid.value = it?.let {
                when {
                    it.isBlank() -> Verification.verifying
                    //!it. -> Verification.codeError//서버에 code가 일치하는지 확인
                    else -> Verification.success
                }
            } ?: Verification.verifying
        }.launchIn(viewModelScope)

    companion object {
        private const val WAIT_TIME = 500L
    }
}

@Stable
internal class UserDataValid(
    nicknameValid : UserValid = UserValid.blank,
    ageValid : UserValid = UserValid.blank,
    heightValid : UserValid = UserValid.blank,
    weightValid : UserValid = UserValid.blank,
    emailValid : Verification = Verification.nothing
){
    val nicknameValid = mutableStateOf(nicknameValid)
    val ageValid = mutableStateOf(ageValid)
    val heightValid = mutableStateOf(heightValid)
    val weightValid = mutableStateOf(weightValid)
    val emailValid = mutableStateOf(emailValid)

    fun isSuccessfulValid() : Boolean =
        (nicknameValid.value == UserValid.success) and (ageValid.value == UserValid.success) and
                (heightValid.value == UserValid.success) and (weightValid.value == UserValid.success) and (emailValid.value == Verification.success)
}

enum class UserValid {
    blank,notValid,success
}

enum class UserEvent {
    nickname,age,height,weight,email,emailCode
}

internal fun UserValid.isError() : Boolean =  this == UserValid.notValid

@Stable
data class SignUpDetail(
    val id : String = "",
    val password : String ="",
    val nickname : String = "",
    val age : String = "",
    val height : String = "",
    val weight : String ="",
    val email : String = "",
    val emailCode : String = ""
)