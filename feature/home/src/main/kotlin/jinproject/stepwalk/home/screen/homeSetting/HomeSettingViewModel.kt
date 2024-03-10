package jinproject.stepwalk.home.screen.homeSetting

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import jinproject.stepwalk.domain.usecase.setting.StepGoalUseCase
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeSettingViewModel @Inject constructor(
    private val stepGoalUseCase: StepGoalUseCase,
) : ViewModel() {

    fun setStepGoal(step: Int) {
        viewModelScope.launch {
            stepGoalUseCase.setStep(step)
        }
    }
}