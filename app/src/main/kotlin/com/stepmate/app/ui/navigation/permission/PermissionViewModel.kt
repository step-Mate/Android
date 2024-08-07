package com.stepmate.app.ui.navigation.permission

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.stepmate.design.component.DialogState
import com.stepmate.home.HealthConnector
import com.stepmate.home.HealthConnector.Companion.healthConnectPermissions
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
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
    private val _notificationPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val notificationPermission get() = _notificationPermission.asStateFlow()

    private val _activityRecognitionPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val activityRecognitionPermission get() = _activityRecognitionPermission.asStateFlow()

    private val _healthConnectPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val healthConnectPermission get() = _healthConnectPermission.asStateFlow()

    private val _exactAlarmPermission: MutableStateFlow<Boolean> = MutableStateFlow(false)
    val exactAlarmPermission get() = _exactAlarmPermission.asStateFlow()

    private val _dialogState = MutableStateFlow(
        DialogState.getInitValue().copy(
            header = "권한을 수락해 주세요.",
            positiveMessage = "설정 하러 가기",
            negativeMessage = "종료",
        )
    )
    val dialogState get() = _dialogState.asStateFlow()

    fun updateDialogState(d: DialogState) {
        _dialogState.update { d }
    }

    init {
        val permissionResults = PermissionRequester.checkAllPermissions(applicationContext)

        updateNotification(permissionResults.notification)
        updateActivityRecognition(permissionResults.activityRecognition)
        updateExactAlarm(permissionResults.exactAlarm)

        viewModelScope.launch {
            updateHealthConnect(checkHealthConnectPermissions())
        }
    }

    private suspend fun checkHealthConnectPermissions() =
        healthConnector.checkPermissions(healthConnectPermissions)

    fun requireInstallHealthApk() = healthConnector.requireInstallHealthApk()
    fun updateNotification(bool: Boolean) = _notificationPermission.update { bool }
    fun updateActivityRecognition(bool: Boolean) = _activityRecognitionPermission.update { bool }
    fun updateExactAlarm(bool: Boolean) = _exactAlarmPermission.update { bool }
    fun updateHealthConnect(bool: Boolean) = _healthConnectPermission.update { bool }

    fun onPermissionResult(permission: Permission, result: Boolean, context: Context) {
        val text = when (permission) {
            Permission.NOTIFICATION -> {
                updateNotification(result)
                "알림"
            }

            Permission.ACTIVITY_RECOGNITION -> {
                updateActivityRecognition(result)
                "신체 활동"
            }

            Permission.EXACT_ALARM -> {
                updateExactAlarm(result)
                "알람 및 리마인더"
            }

            Permission.HEALTH_CONNECT -> {
                updateHealthConnect(result)
                "헬스 커넥트"
            }
        }

        val healthConnectPermissionMessage =
            "헬스 커넥트 권한이 수락되지 않았어요.\n 헬스 커넥트 설정 > 앱 권한 > StepMate > 걸음수 권한 허용 으로 변경해 주세요."
        val systemPermissionMessage = "${text}이 수락되지 않았어요.\n 앱 설정 > 앱 권한 > $text 허용 으로 변경해 주세요."

        if (!result)
            _dialogState.update { state ->
                state.copy(
                    content =
                    if (permission == Permission.HEALTH_CONNECT)
                        healthConnectPermissionMessage
                    else
                        systemPermissionMessage,
                    onPositiveCallback = {
                        when (permission) {
                            Permission.NOTIFICATION, Permission.ACTIVITY_RECOGNITION, Permission.EXACT_ALARM -> context.requireAppSettings()
                            Permission.HEALTH_CONNECT -> healthConnector.requireInstallHealthApk()
                        }
                        updateDialogState(state.copy(isShown = false))
                    },
                    onNegativeCallback = {
                        updateDialogState(state.copy(isShown = false))
                    },
                    isShown = true,
                )
            }
    }

    private fun Context.requireAppSettings() =
        startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
            data = Uri.fromParts("package", packageName, null)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK
        })

    enum class Permission {
        NOTIFICATION,
        ACTIVITY_RECOGNITION,
        EXACT_ALARM,
        HEALTH_CONNECT;

        fun toAppSettingsPermissionOrNull() = when (this) {
            NOTIFICATION -> if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) Manifest.permission.POST_NOTIFICATIONS else null
            ACTIVITY_RECOGNITION -> Manifest.permission.ACTIVITY_RECOGNITION
            else -> null
        }

        fun toHealthConnectPermissionOrNull() = when (this) {
            HEALTH_CONNECT -> healthConnectPermissions
            else -> null
        }
    }
}