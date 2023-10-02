package jinproject.stepwalk.app.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class StepWalkViewModel @Inject constructor(): ViewModel() {
    private val _state = MutableSharedFlow<NetworkState>()
    val state get() = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NetworkState.Loading
    )

    fun setNetworkState(state: NetworkState) {
        viewModelScope.launch {
            _state.emit(state)
        }
    }
}