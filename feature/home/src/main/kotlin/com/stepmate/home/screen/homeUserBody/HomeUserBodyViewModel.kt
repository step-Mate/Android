package com.stepmate.home.screen.homeUserBody

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import com.stepmate.domain.usecase.user.GetBodyDataUseCases
import com.stepmate.domain.usecase.user.SetBodyLocalUseCases
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeUserBodyViewModel @Inject constructor(
    private val getBodyDataUseCases: GetBodyDataUseCases,
    private val setBodyLocalUseCases: SetBodyLocalUseCases,
) : ViewModel() {

    private val _age = MutableStateFlow(0)
    val age get() = _age.asStateFlow()

    private val _height = MutableStateFlow(0)
    val height get() = _height.asStateFlow()

    private val _weight = MutableStateFlow(0)
    val weight get() = _weight.asStateFlow()

    private val _bottomSheetType = MutableStateFlow(BottomSheetType.Age)
    val bottomSheetType get() = _bottomSheetType

    init {
        getUserBody()
    }

    fun updateAge(age: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            setBodyLocalUseCases.setAge(age)
        }
    }

    fun updateWeight(weight: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            setBodyLocalUseCases.setWeight(weight)
        }
    }

    fun updateHeight(height: Int) {
        viewModelScope.launch(Dispatchers.IO) {
            setBodyLocalUseCases.setHeight(height)
        }
    }

    fun updateBottomSheetType(type: BottomSheetType) = _bottomSheetType.update { type }

    private fun getUserBody() = getBodyDataUseCases().onEach { bodyData ->
        _age.update { bodyData.age }
        _height.update { bodyData.height }
        _weight.update { bodyData.weight }
    }.launchIn(viewModelScope)

    enum class BottomSheetType(val display: String) {
        Age("나이"),
        Weight("몸무게"),
        Height("키")
    }

}