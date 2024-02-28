package com.beank.profile.screen.edit

import androidx.compose.runtime.Stable
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.core.catchDataFlow
import jinproject.stepwalk.core.isValidAge
import jinproject.stepwalk.core.isValidHeight
import jinproject.stepwalk.core.isValidNickname
import jinproject.stepwalk.core.isValidWeight
import jinproject.stepwalk.domain.model.BodyData
import jinproject.stepwalk.domain.model.onException
import jinproject.stepwalk.domain.model.onSuccess
import jinproject.stepwalk.domain.usecase.auth.CheckNicknameUseCase
import jinproject.stepwalk.domain.usecase.auth.GetBodyDataUseCases
import jinproject.stepwalk.domain.usecase.auth.SetBodyDataUseCases
import jinproject.stepwalk.domain.usecase.user.GetDesignationsUseCases
import jinproject.stepwalk.domain.usecase.user.SelectDesignationUseCases
import jinproject.stepwalk.domain.usecase.user.SetBodyUseCases
import jinproject.stepwalk.domain.usecase.user.UpdateNicknameUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.zip
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
    private val setBodyDataUseCases: SetBodyDataUseCases,//해당 부분 통합??
    private val setBodyUseCases: SetBodyUseCases,
    getDesignationsUseCases: GetDesignationsUseCases,
    private val updateNicknameUseCases: UpdateNicknameUseCases,
    private val selectDesignationUseCases: SelectDesignationUseCases
) : ViewModel() {
    private val originalNickname: String
    val anonymousState: Boolean

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

    init {
        originalNickname = savedStateHandle.get<String>("nickname") ?: ""
        _nickname.value = originalNickname
        anonymousState = savedStateHandle.get<Boolean>("anonymous") ?: true
        val tempDesignation = savedStateHandle.get<String>("designation") ?: ""
        _designation.value = if (tempDesignation == "-1") "" else tempDesignation
        viewModelScope.launch(Dispatchers.IO) {
            val response = getBodyDataUseCases()
            _age.update { response.age.toString() }
            _height.update { response.height.toString() }
            _weight.update { response.weight.toString() }
            if (!anonymousState)
                checkNicknameValid()
        }
        if (!anonymousState) {
            getDesignationsUseCases().onEach { designationState ->
                _designationList.update { designationState.list }
                _uiState.emit(UiState.Success)
            }.catchDataFlow(
                action = { e ->
                    e
                },
                onException = { e ->
                    _uiState.emit(UiState.Error(e))
                }
            )
        }
    }

    @OptIn(FlowPreview::class)
    private suspend fun checkNicknameValid() = _nickname
        .debounce(800)
        .filter { it.isNotEmpty() }
        .distinctUntilChanged()
        .onEach { nickname ->
            if (nickname != originalNickname) {
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
                    if (originalNickname != nickname.value) {
                        updateNicknameUseCases(nickname.value).zip(fetchUserInfo()) { nicknameState, state ->
                            if (nicknameState && state) {
                                _saveState.update { true }
                            }
                        }.catchDataFlow(
                            action = { e ->
                                e
                            },
                            onException = { e ->
                                _saveState.update { false }
                            }
                        ).launchIn(viewModelScope)
                    } else {
                        fetchUserInfo().onEach { state ->
                            if (state)
                                _saveState.update { true }
                        }.catchDataFlow(
                            action = { e ->
                                e
                            },
                            onException = { e ->
                                _saveState.update { false }
                            }
                        ).launchIn(viewModelScope)
                    }
                } else {
                    viewModelScope.launch(Dispatchers.IO) {
                        setBodyDataUseCases(
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

    private fun fetchUserInfo() = selectDesignationUseCases(designation.value)
        .zip(
            setBodyUseCases(
                BodyData(
                    age.value.toInt(), height.value.toInt(), weight.value.toInt()
                )
            )
        ) { designationState, bodyState ->
            viewModelScope.launch(Dispatchers.IO) {
                setBodyDataUseCases(
                    BodyData(
                        age.value.toInt(), height.value.toInt(), weight.value.toInt()
                    )
                )
            }
            designationState && bodyState
        }

    @Stable
    sealed class UiState {
        data object Loading : UiState()
        data object Success : UiState()
        data class Error(val exception: Throwable, val uuid: UUID = UUID.randomUUID()) : UiState()
    }

}