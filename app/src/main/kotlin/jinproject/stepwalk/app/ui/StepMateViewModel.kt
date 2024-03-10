package jinproject.stepwalk.app.ui

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import androidx.core.content.ContextCompat
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jinproject.stepwalk.home.HealthConnector
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
internal class StepMateViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val healthConnector: HealthConnector,
): ViewModel() {
    private val _state = MutableSharedFlow<NetworkState>()
    val state get() = _state.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = NetworkState.Loading
    )

    private val _permissionState = MutableStateFlow(false)
    val permissionState get() = _permissionState.asStateFlow()

    fun setNetworkState(state: NetworkState) {
        viewModelScope.launch {
            _state.emit(state)
        }
    }

    suspend fun checkPermission() {
        val isNotificationGranted = Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU && ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED

        val isActivityRecognitionGranted = ContextCompat.checkSelfPermission(applicationContext, Manifest.permission.ACTIVITY_RECOGNITION)== PackageManager.PERMISSION_GRANTED

        val healthDataTypes = setOf(
            StepsRecord::class,
        )

        val healthConnectPermissions: Set<String> =
            healthDataTypes.map {
                HealthPermission.getReadPermission(it)
            }.toMutableSet().apply {
                addAll(healthDataTypes.map { HealthPermission.getWritePermission(it) })
            }.toSet()

        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        val isExactAlarmGranted = Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms()

        val isHealthConnectGranted = healthConnector.checkPermissions(healthConnectPermissions)

        _permissionState.update { isNotificationGranted && isActivityRecognitionGranted && isExactAlarmGranted && isHealthConnectGranted }
    }
}