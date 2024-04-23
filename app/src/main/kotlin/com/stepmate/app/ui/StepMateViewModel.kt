package com.stepmate.app.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class StepMateViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val isBodyDataExist: Boolean = savedStateHandle["isBodyDataExist"] ?: false

    private val _isNeedReLogin = MutableStateFlow(false)
    val isNeedReLogin get() = _isNeedReLogin.asStateFlow()

    fun updateIsNeedLogin(bool: Boolean) = _isNeedReLogin.update { bool }
}