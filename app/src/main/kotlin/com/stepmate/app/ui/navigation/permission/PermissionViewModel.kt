package com.stepmate.app.ui.navigation.permission

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import com.stepmate.home.HealthConnector
import com.stepmate.home.HealthConnector.Companion.healthConnectPermissions
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

    private val _notificationPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val notificationPermission get() = _notificationPermission.asStateFlow()

    private val _activityRecognitionPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val activityRecognitionPermission get() = _activityRecognitionPermission.asStateFlow()

    private val _healthConnectPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val healthConnectPermission get() = _healthConnectPermission.asStateFlow()

    private val _exactAlarmPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val exactAlarmPermission get() = _exactAlarmPermission.asStateFlow()

    init {
        _notificationPermission.update {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                PermissionRequester.checkNotification(applicationContext)
            else
                true
        }

        if (PermissionRequester.checkActivityRecognition(applicationContext))
            _activityRecognitionPermission.update { true }

        _exactAlarmPermission.update {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PermissionRequester.checkExactAlarm(applicationContext)
            else
                true
        }

        viewModelScope.launch {
            if (checkHealthConnectPermissions())
                _healthConnectPermission.update { true }
        }
    }

    private suspend fun checkHealthConnectPermissions() =
        healthConnector.checkPermissions(healthConnectPermissions)

    fun requireInstallHealthApk() = healthConnector.requireInstallHealthApk()
    fun updateNotification(bool: Boolean) = _notificationPermission.update { bool }
    fun updateActivityRecognition(bool: Boolean) = _activityRecognitionPermission.update { bool }
    fun updateExactAlarm(bool: Boolean) = _exactAlarmPermission.update { bool }
    fun updateHealthConnect(bool: Boolean) = _healthConnectPermission.update { bool }
}