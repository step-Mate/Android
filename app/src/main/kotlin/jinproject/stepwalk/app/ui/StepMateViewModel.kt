package jinproject.stepwalk.app.ui

import android.content.Context
import android.os.Build
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import jinproject.stepwalk.app.ui.navigation.permission.PermissionRequester
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.HealthConnector.Companion.healthConnectPermissions
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
internal class StepMateViewModel @Inject constructor(
    @ApplicationContext private val applicationContext: Context,
    private val healthConnector: HealthConnector,
) : ViewModel() {

    private val _permissionState = MutableStateFlow(false)
    val permissionState get() = _permissionState.asStateFlow()

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

        val isHealthConnectGranted = healthConnector.checkPermissions(healthConnectPermissions)

        _permissionState.update { isNotificationGranted && isActivityRecognitionGranted && isExactAlarmGranted && isHealthConnectGranted }
    }
}