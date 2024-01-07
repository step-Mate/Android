package com.beank.login.screen.signupdetail

import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.beank.login.utils.isValidDouble
import com.beank.login.utils.isValidInt
import com.beank.login.utils.isValidNickname
import dagger.hilt.android.lifecycle.HiltViewModel
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

    val valids = UserDataValid()

    //비었는지,유효한지,
    private val debouncedNicknameFilter : Flow<String?> = nickname
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    private val debouncedAgeFilter : Flow<String?> = age
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    private val debouncedHeightFilter : Flow<String?> = height
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    private val debouncedWeightFilter : Flow<String?> = weight
        .debounce(WAIT_TIME)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()

    init {
        checkNicknameValid()
        checkAgeValid()
        checkHeightValid()
        checkWeightValid()
    }

    fun updateUserEvent(event : UserEvent, value : String){
        when (event){
            UserEvent.nickname -> _nickname.value = value
            UserEvent.age -> _age.value = value
            UserEvent.height -> _height.value = value
            UserEvent.weight -> _weight.value = value
        }
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

    companion object {
        private const val WAIT_TIME = 500L
    }

}

@Stable
internal class UserDataValid(
    nicknameValid : UserValid = UserValid.blank,
    ageValid : UserValid = UserValid.blank,
    heightValid : UserValid = UserValid.blank,
    weightValid : UserValid = UserValid.blank
){
    val nicknameValid = mutableStateOf(nicknameValid)
    val ageValid = mutableStateOf(ageValid)
    val heightValid = mutableStateOf(heightValid)
    val weightValid = mutableStateOf(weightValid)

    fun isSuccessfulValid() : Boolean =
        (nicknameValid.value == UserValid.success) and (ageValid.value == UserValid.success) and (heightValid.value == UserValid.success) and (weightValid.value == UserValid.success)
}

enum class UserValid {
    blank,notValid,success
}

enum class UserEvent {
    nickname,age,height,weight
}

internal fun UserValid.isError() : Boolean =  this == UserValid.notValid

@Stable
data class SignUpDetail(
    val id : String = "",
    val password : String ="",
    val nickname : String = "",
    val age : String = "",
    val height : String = "",
    val weight : String =""
)