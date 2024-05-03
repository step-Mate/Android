package com.stepmate.app.ui.navigation.permission

import android.Manifest
import android.app.AlarmManager
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stepmate.core.SnackBarMessage
import com.stepmate.design.component.DescriptionLargeText
import com.stepmate.design.component.DescriptionSmallText
import com.stepmate.design.component.DialogState
import com.stepmate.design.component.HeadlineText
import com.stepmate.design.component.HorizontalWeightSpacer
import com.stepmate.design.component.SelectionButton
import com.stepmate.design.component.StepMateDialog
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.design.component.clickableAvoidingDuplication
import com.stepmate.design.component.layout.DefaultLayout
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.home.HealthConnector.Companion.healthConnectPermissions

@Composable
internal fun PermissionScreen(
    modifier: Modifier = Modifier,
    permissionViewModel: PermissionViewModel = hiltViewModel(),
    showSnackBar: (SnackBarMessage) -> Unit,
    navigateToHomeGraph: () -> Unit,
) {
    val notificationPermission by permissionViewModel.notificationPermission.collectAsStateWithLifecycle()
    val activityRecognitionPermission by permissionViewModel.activityRecognitionPermission.collectAsStateWithLifecycle()
    val exactAlarmPermission by permissionViewModel.exactAlarmPermission.collectAsStateWithLifecycle()
    val healthConnectPermission by permissionViewModel.healthConnectPermission.collectAsStateWithLifecycle()
    val dialogState by permissionViewModel.dialogState.collectAsStateWithLifecycle()

    SideEffect {
        if (notificationPermission && activityRecognitionPermission && exactAlarmPermission && healthConnectPermission)
            navigateToHomeGraph()
    }

    PermissionScreen(
        modifier = modifier,
        healthConnectPermissionContract = permissionViewModel::healthConnectPermissionContract.get(),
        requireInstallHealthApk = permissionViewModel::requireInstallHealthApk,
        showSnackBar = showSnackBar,
        notificationPermission = notificationPermission,
        activityRecognitionPermission = activityRecognitionPermission,
        healthConnectPermission = healthConnectPermission,
        exactAlarmPermission = exactAlarmPermission,
        dialogState = dialogState,
        updateExactAlarm = permissionViewModel::updateExactAlarm,
        updateDialogState = permissionViewModel::updateDialogState,
        onPermissionResult = permissionViewModel::onPermissionResult,
    )
}

@Composable
private fun PermissionScreen(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current,
    healthConnectPermissionContract: ActivityResultContract<Set<String>, Set<String>>,
    requireInstallHealthApk: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
    notificationPermission: Boolean,
    activityRecognitionPermission: Boolean,
    healthConnectPermission: Boolean,
    exactAlarmPermission: Boolean,
    dialogState: DialogState,
    updateExactAlarm: (Boolean) -> Unit,
    updateDialogState: (DialogState) -> Unit,
    onPermissionResult: (PermissionViewModel.Permission, Boolean, Context) -> Unit,
) {
    StepMateDialog(dialogState = dialogState) {
        updateDialogState(dialogState.copy(isShown = false))
    }

    var isRequestedExactAlarm by remember {
        mutableStateOf(Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    }

    LifecycleStartEffect(key1 = isRequestedExactAlarm) {
        if (isRequestedExactAlarm && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (alarmManager.canScheduleExactAlarms())
                updateExactAlarm(true)
            else
                onPermissionResult(
                    PermissionViewModel.Permission.EXACT_ALARM,
                    false,
                    context
                )

            isRequestedExactAlarm = false
        }
        onStopOrDispose { }
    }

    DefaultLayout(
        modifier = modifier,
    ) {
        HeadlineText(
            text = "권한 요청", modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
        )

        VerticalSpacer(height = 40.dp)
        DescriptionSmallText(text = "앱을 이용하기 위해 필요한 권한을 설명해 드릴게요.\n권한을 수락하지 않으시면 서비스를 이용하실 수 없어요.")
        VerticalSpacer(height = 40.dp)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            PermissionActivityResultDescription(
                buttonStatus = notificationPermission,
                headline = "알림",
                type = PermissionViewModel.Permission.NOTIFICATION,
                desc = "실시간 만보기 수치 표시, 미션 달성, 친구 신청, 공지 사항 등의 기능을 수신하기 위한 알림 권한이 필요해요.",
                permissionContract = ActivityResultContracts.RequestPermission(),
                onPermissionResult = { result ->
                    onPermissionResult(
                        PermissionViewModel.Permission.NOTIFICATION,
                        result,
                        context
                    )
                },
                permission = Manifest.permission.POST_NOTIFICATIONS,
                showSnackBar = showSnackBar,
            )
        VerticalSpacer(height = 40.dp)
        PermissionActivityResultDescription(
            buttonStatus = activityRecognitionPermission,
            type = PermissionViewModel.Permission.ACTIVITY_RECOGNITION,
            headline = "신체 활동",
            desc = "만보기 기능을 이용하기 위해 실시간으로 스마트폰의 센서로 부터 걸음수를 수집하고 있어요.\n해당 권한을 수락해 주셔야 걸음수를 수집할 수 있어요.",
            permissionContract = ActivityResultContracts.RequestPermission(),
            onPermissionResult = { result ->
                onPermissionResult(
                    PermissionViewModel.Permission.ACTIVITY_RECOGNITION,
                    result,
                    context
                )
            },
            permission = Manifest.permission.ACTIVITY_RECOGNITION,
            showSnackBar = showSnackBar,
        )
        VerticalSpacer(height = 40.dp)
        PermissionActivityResultDescription(
            buttonStatus = healthConnectPermission,
            type = PermissionViewModel.Permission.HEALTH_CONNECT,
            headline = "헬스 커넥트",
            desc = "수집한 걸음수를 저장하기 위해 헬스 커넥트에 읽고 쓸수 있는 권한이 필요해요.\nStepMate는 차후 걸음수 뿐만 아니라 다양한 헬스 데이터를 활용하여 더 좋은 경험을 제공해 드릴 예정이에요.",
            permissionContract = healthConnectPermissionContract,
            onPermissionResult = { result ->
                Log.d("test", result.toString())
                val isGranted = result.containsAll(
                    healthConnectPermissions
                )

                onPermissionResult(
                    PermissionViewModel.Permission.HEALTH_CONNECT,
                    isGranted,
                    context
                )
            },
            permission = healthConnectPermissions,
            showSnackBar = showSnackBar,
            onException = { t ->
                if (t is ActivityNotFoundException)
                    requireInstallHealthApk()
            }
        )
        VerticalSpacer(height = 40.dp)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
            PermissionDescription(
                buttonStatus = exactAlarmPermission,
                headline = "알람 및 리마인더 (정확한 알람)",
                desc = "수집된 걸음수를 매일 자정에 초기화 하기 위해 정확한 시간에 울리는 알람 권한이 필요해요.",
                requestPermission = {
                    context.startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                    isRequestedExactAlarm = true
                },
                showSnackBar = showSnackBar,
            )

        VerticalSpacer(height = 40.dp)
    }
}

@Composable
internal fun <I, O> PermissionActivityResultDescription(
    context: Context = LocalContext.current,
    buttonStatus: Boolean,
    type: PermissionViewModel.Permission,
    headline: String,
    desc: String,
    permissionContract: ActivityResultContract<I, O>,
    onPermissionResult: (O) -> Unit,
    permission: I,
    showSnackBar: (SnackBarMessage) -> Unit,
    onException: (Throwable) -> Unit = {},
) {
    val permissionLauncher =
        rememberLauncherForActivityResult(contract = permissionContract) { result ->
            onPermissionResult(result)
        }

    PermissionDescription(
        buttonStatus = buttonStatus,
        headline = headline,
        desc = desc,
        requestPermission = {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R)
                runCatching {
                    permissionLauncher.launch(permission)
                }.onFailure { e ->
                    onException(e)
                }
            else
                ActivityCompat.requestPermissions(
                    context as ComponentActivity,
                    when (permission) {
                        is Set<*> -> permission.map { it.toString() }.toTypedArray()
                        is String -> arrayOf(permission)
                        else -> emptyArray()
                    },
                    when (type) {
                        PermissionViewModel.Permission.ACTIVITY_RECOGNITION -> PermissionViewModel.ACTIVITY_RECOGNITION_CODE
                        PermissionViewModel.Permission.HEALTH_CONNECT -> PermissionViewModel.HEALTH_CONNECT_CODE
                        else -> 0
                    }
                )
        },
        showSnackBar = showSnackBar,
    )
}

@Composable
internal fun PermissionDescription(
    buttonStatus: Boolean,
    headline: String,
    desc: String,
    requestPermission: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                1.5.dp,
                MaterialTheme.colorScheme.outline,
                RoundedCornerShape(20.dp),
            )
            .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Row {
            DescriptionLargeText(
                text = headline,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            HorizontalWeightSpacer(float = 1f)
            SelectionButton(
                buttonStatus = buttonStatus,
                modifier = Modifier
                    .width(50.dp)
                    .height(25.dp)
                    .clickableAvoidingDuplication {
                        if (!buttonStatus)
                            requestPermission()
                        else
                            showSnackBar(
                                SnackBarMessage(
                                    headerMessage = "승인된 권한을 거부할 수 없어요.",
                                    contentMessage = "권한을 제거하시려면, 앱 설정 > 앱 권한 > 권한 거부 로 변경해주세요."
                                )
                            )
                    },
            )
        }
        VerticalSpacer(height = 20.dp)
        DescriptionSmallText(text = desc)
    }
}

@Composable
@Preview
private fun PreviewPermissionScreen() = StepMateTheme {
    PermissionScreen(
        healthConnectPermissionContract = PermissionController.createRequestPermissionResultContract(),
        showSnackBar = {},
        notificationPermission = true,
        activityRecognitionPermission = false,
        healthConnectPermission = false,
        exactAlarmPermission = true,
        dialogState = DialogState.getInitValue(),
        updateExactAlarm = {},
        requireInstallHealthApk = {},
        updateDialogState = {},
        onPermissionResult = { _, _, _ -> }
    )
}