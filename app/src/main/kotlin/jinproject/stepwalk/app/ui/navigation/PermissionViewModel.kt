package jinproject.stepwalk.app.ui.navigation

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
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PermissionViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val healthConnector: HealthConnector,
) : ViewModel() {
    val healthConnectPermissionContract = healthConnector.requestPermissionsActivityContract()

    private val healthDataTypes = setOf(
        StepsRecord::class,
    )

    val healthConnectPermissions: Set<String> =
        healthDataTypes.map {
            HealthPermission.getReadPermission(it)
        }.toMutableSet().apply {
            addAll(healthDataTypes.map { HealthPermission.getWritePermission(it) })
        }.toSet()

    private val _notificationPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val notificationPermission get() = _notificationPermission.asStateFlow()

    private val _activityRecognitionPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val activityRecognitionPermission get() = _activityRecognitionPermission.asStateFlow()

    private val _healthConnectPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val healthConnectPermission get() = _healthConnectPermission.asStateFlow()

    private val _exactAlarmPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val exactAlarmPermission get() = _exactAlarmPermission.asStateFlow()

    init {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            val result = ContextCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            )
            if (result == PackageManager.PERMISSION_GRANTED)
                _notificationPermission.update { true }
        }

        val isActivityRecognitionGranted = ContextCompat.checkSelfPermission(
            applicationContext,
            Manifest.permission.ACTIVITY_RECOGNITION
        )
        if (isActivityRecognitionGranted == PackageManager.PERMISSION_GRANTED)
            _activityRecognitionPermission.update { true }

        viewModelScope.launch {
            if (checkHealthConnectPermissions())
                _healthConnectPermission.update { true }
        }

        val alarmManager =
            applicationContext.getSystemService(Context.ALARM_SERVICE) as AlarmManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S && alarmManager.canScheduleExactAlarms())
            _exactAlarmPermission.update { true }
    }

    private suspend fun checkHealthConnectPermissions() =
        healthConnector.checkPermissions(healthConnectPermissions)

    fun requireInstallHealthApk() = healthConnector.requireInstallHealthApk()

    fun updateNotification(bool: Boolean) = _notificationPermission.update { bool }
    fun updateActivityRecognition(bool: Boolean) = _activityRecognitionPermission.update { bool }
    fun updateExactAlarm(bool: Boolean) = _exactAlarmPermission.update { bool }
    fun updateHealthConnect(bool: Boolean) = _healthConnectPermission.update { bool }
}