package com.stepmate.app.ui

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import com.stepmate.app.ui.navigation.permission.PermissionRequester
import com.stepmate.domain.usecase.user.GetBodyDataUseCase
import com.stepmate.home.HealthConnector
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class StepMateViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val healthConnector: HealthConnector,
    private val getBodyDataUseCase: GetBodyDataUseCase,
) : ViewModel() {

    private val _isNeedReLogin = MutableStateFlow(false)
    val isNeedReLogin get() = _isNeedReLogin.asStateFlow()

    fun updateIsNeedLogin(bool: Boolean) = _isNeedReLogin.update { bool }

    fun updateActivityRecognition(bool: Boolean) {
        _startDestinationInfo.update { info ->
            info.copy(
                underApi31HasPermission = info.underApi31HasPermission.copy(
                    isActivityRecognitionGranted = bool
                )
            )
        }
    }

    fun updateHealthConnect(bool: Boolean) {
        _startDestinationInfo.update { info ->
            info.copy(
                underApi31HasPermission = info.underApi31HasPermission.copy(
                    isHealthConnectGranted = bool
                )
            )
        }
    }

    private val _startDestinationInfo = MutableStateFlow(StartDestinationInfo())

    val startDestinationInfo = _startDestinationInfo.asStateFlow()

    private suspend fun isBodyDataExist() = getBodyDataUseCase().map { bodyData ->
        bodyData.age != 0 && bodyData.height != 0 && bodyData.weight != 0
    }.first()

    suspend fun checkPermission() {
        val isNotificationGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
                PermissionRequester.checkNotification(applicationContext)
            else
                true

        val isActivityRecognitionGranted =
            PermissionRequester.checkActivityRecognition(applicationContext)

        val isExactAlarmGranted =
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S)
                PermissionRequester.checkExactAlarm(applicationContext)
            else
                true

        val isHealthConnectGranted = runCatching {
            healthConnector.checkPermissions(HealthConnector.healthConnectPermissions)
        }.getOrDefault(false)

        _startDestinationInfo.update { info ->
            info.copy(
                hasPermission = isNotificationGranted && isActivityRecognitionGranted && isExactAlarmGranted && isHealthConnectGranted,
                hasBodyData = isBodyDataExist(),
            )
        }
    }
}

data class StartDestinationInfo(
    val hasPermission: Boolean = false,
    val hasBodyData: Boolean = false,
    val underApi31HasPermission: UnderApi31Permission = UnderApi31Permission(),
)

data class UnderApi31Permission(
    val isActivityRecognitionGranted: Boolean = false,
    val isHealthConnectGranted: Boolean = false,
)