package com.stepmate.profile.screen.edit

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepmate.core.SnackBarMessage
import com.stepmate.core.catchDataFlow
import com.stepmate.core.isValidAge
import com.stepmate.core.isValidHeight
import com.stepmate.core.isValidNickname
import com.stepmate.core.isValidWeight
import com.stepmate.domain.model.BodyData
import com.stepmate.domain.model.onException
import com.stepmate.domain.model.onSuccess
import com.stepmate.domain.usecase.auth.CheckNicknameUseCase
import com.stepmate.domain.usecase.user.GetBodyDataUseCases
import com.stepmate.domain.usecase.user.GetDesignationsUseCases
import com.stepmate.domain.usecase.user.SelectDesignationUseCases
import com.stepmate.domain.usecase.user.SetBodyLocalUseCases
import com.stepmate.domain.usecase.user.SetBodyUseCases
import com.stepmate.domain.usecase.user.UpdateNicknameUseCases
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

sealed interface EditUserEvent {
    data object Save : EditUserEvent
    data class Nickname(val value: String) : EditUserEvent
    data class Age(val value: String) : EditUserEvent
    data class Height(val value: String) : EditUserEvent
    data class Weight(val value: String) : EditUserEvent
    data class Designation(val value: String) : EditUserEvent
}

enum class Valid {
    Duplication, NotValid, Success
}

@HiltViewModel
class EditViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val checkNicknameUseCase: CheckNicknameUseCase,
    private val getBodyDataUseCases: GetBodyDataUseCases,
    private val setBodyUseCases: SetBodyUseCases,
    private val setBodyLocalUseCases: SetBodyLocalUseCases,
    getDesignationsUseCases: GetDesignationsUseCases,
    private val updateNicknameUseCases: UpdateNicknameUseCases,
    private val selectDesignationUseCases: SelectDesignationUseCases
) : ViewModel() {

    private val originalNickname: String = savedStateHandle.get<String>("nickname") ?: ""
    val anonymousState: Boolean = savedStateHandle.get<Boolean>("anonymous") ?: true

    private val _saveState = MutableStateFlow(false)
    val saveState get() = _saveState.asStateFlow()

    private val _uiState: MutableSharedFlow<UiState> = MutableSharedFlow(replay = 1)
    val uiState: SharedFlow<UiState> get() = _uiState.asSharedFlow()

    private val _nicknameValid = MutableStateFlow(Valid.Success)
    val nicknameValid get() = _nicknameValid.asStateFlow()

    private val _ageValid = MutableStateFlow(false)
    val ageValid get() = _ageValid.asStateFlow()

    private val _heightValid = MutableStateFlow(false)
    val heightValid get() = _heightValid.asStateFlow()

    private val _weightValid = MutableStateFlow(false)
    val weightValid get() = _weightValid.asStateFlow()

    private val _nickname = MutableStateFlow("")
    val nickname get() = _nickname.asStateFlow()

    private val _designation = MutableStateFlow("")
    val designation get() = _designation.asStateFlow()

    private val _designationList = MutableStateFlow<List<String>>(emptyList())
    val designationList get() = _designationList.asStateFlow()

    private val _age = MutableStateFlow("")
    val age get() = _age.asStateFlow()

    private val _height = MutableStateFlow("")
    val height get() = _height.asStateFlow()

    private val _weight = MutableStateFlow("")
    val weight get() = _weight.asStateFlow()

    private val _snackBarState: MutableSharedFlow<SnackBarMessage> = MutableSharedFlow(replay = 0)
    val snackBarState: SharedFlow<SnackBarMessage> get() = _snackBarState.asSharedFlow()

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, t ->
        viewModelScope.launch {
            _snackBarState.emit(
                SnackBarMessage(
                    headerMessage = "일시적인 장애가 발생했어요.",
                    contentMessage = t.message.toString()
                )
            )
        }
    }

    init {
        _nickname.value = if (originalNickname == "-") "" else originalNickname
        val tempDesignation = savedStateHandle.get<String>("designation") ?: ""
        _designation.value = if (tempDesignation == "-1") "" else tempDesignation

        viewModelScope.launch(Dispatchers.IO) {
            val response = getBodyDataUseCases().first()
            _age.update { response.age.toString() }
            _height.update { response.height.toString() }
            _weight.update { response.weight.toString() }
            if (!anonymousState)
                checkNicknameValid()
        }
        if (!anonymousState) {//로그인 상태일시
            getDesignationsUseCases().onEach { designationList ->
                _designationList.update { designationList.filter { !it.contains("주간") } }
                _uiState.emit(UiState.Success)
            }.catchDataFlow(
                action = { e ->
                    e
                },
                onException = { e ->
                    _uiState.emit(UiState.Error(e))
                }
            ).launchIn(viewModelScope)
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun checkNicknameValid() = _nickname
        .debounce(800)
        .distinctUntilChanged()
        .onEach { nickname ->
            if (nickname.isEmpty()) {
                _nicknameValid.value = Valid.NotValid
            } else if (nickname != originalNickname) {
                if (nickname.isValidNickname()) {
                    _nicknameValid.value = checkDuplicationNickname(nickname)
                } else {
                    _nicknameValid.value = Valid.NotValid
                }
            } else {
                _nicknameValid.value = Valid.Success
            }
        }.launchIn(viewModelScope)

    private suspend fun checkDuplicationNickname(nickname: String): Valid {
        var result = Valid.Duplication
        checkNicknameUseCase(nickname)
            .onSuccess { result = Valid.Success }
            .onException { code, message ->
                result = Valid.Duplication
            }
        return result
    }


    fun onEvent(event: EditUserEvent) {
        when (event) {
            EditUserEvent.Save -> {
                if (!anonymousState) {
                    viewModelScope.launch(coroutineExceptionHandler + Dispatchers.IO) {
                        if (originalNickname != nickname.value)
                            updateNicknameUseCases(nickname.value)
                        selectDesignationUseCases(designation.value)
                        setBodyUseCases(
                            BodyData(
                                age.value.toInt(), height.value.toInt(), weight.value.toInt()
                            )
                        )
                        _saveState.update { true }
                    }
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        setBodyLocalUseCases(
                            BodyData(
                                age.value.toInt(), height.value.toInt(), weight.value.toInt()
                            )
                        )
                    }
                    _saveState.update { true }
                }
            }

            is EditUserEvent.Nickname -> {
                _nickname.value = event.value
            }

            is EditUserEvent.Age -> {
                _age.update { event.value }
                if (event.value.isNotEmpty()) {
                    _ageValid.value = !event.value.isValidAge()
                }
            }

            is EditUserEvent.Height -> {
                _height.update { event.value }
                if (event.value.isNotEmpty()) {
                    _heightValid.value = !event.value.isValidHeight()
                }
            }

            is EditUserEvent.Weight -> {
                _weight.update { event.value }
                if (event.value.isNotEmpty()) {
                    _weightValid.value = !event.value.isValidWeight()
                }
            }

            is EditUserEvent.Designation -> {
                _designation.value = event.value
            }
        }
    }

    @Stable
    sealed class UiState {
        data object Loading : UiState()
        data object Success : UiState()
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }

}