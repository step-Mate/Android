package jinproject.stepwalk.app.ui.navigation.permission

import android.Manifest
import android.app.AlarmManager
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
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
import androidx.health.connect.client.PermissionController
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import androidx.navigation.navOptions
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.DialogState
import jinproject.stepwalk.design.component.HeadlineText
import jinproject.stepwalk.design.component.HorizontalWeightSpacer
import jinproject.stepwalk.design.component.SelectionButton
import jinproject.stepwalk.design.component.StepMateDialog
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.component.clickableAvoidingDuplication
import jinproject.stepwalk.design.component.layout.DefaultLayout
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.HealthConnector.Companion.healthConnectPermissions

internal const val permissionRoute = "permission"

@Composable
internal fun PermissionScreen(
    permissionViewModel: PermissionViewModel = hiltViewModel(),
    showSnackBar: (SnackBarMessage) -> Unit,
    navigateToHome: (NavOptions?) -> Unit,
) {
    val notificationPermission by permissionViewModel.notificationPermission.collectAsStateWithLifecycle()
    val activityRecognitionPermission by permissionViewModel.activityRecognitionPermission.collectAsStateWithLifecycle()
    val exactAlarmPermission by permissionViewModel.exactAlarmPermission.collectAsStateWithLifecycle()
    val healthConnectPermission by permissionViewModel.healthConnectPermission.collectAsStateWithLifecycle()

    SideEffect {
        if (notificationPermission && activityRecognitionPermission && exactAlarmPermission && healthConnectPermission)
            navigateToHome(
                navOptions {
                    popUpTo(permissionRoute) {
                        inclusive = true
                    }
                    launchSingleTop = true
                }
            )
    }

    PermissionScreen(
        healthConnectPermissionContract = permissionViewModel::healthConnectPermissionContract.get(),
        requireInstallHealthApk = permissionViewModel::requireInstallHealthApk,
        showSnackBar = showSnackBar,
        notificationPermission = notificationPermission,
        activityRecognitionPermission = activityRecognitionPermission,
        healthConnectPermission = healthConnectPermission,
        exactAlarmPermission = exactAlarmPermission,
        updateNotification = permissionViewModel::updateNotification,
        updateActivityRecognition = permissionViewModel::updateActivityRecognition,
        updateHealthConnect = permissionViewModel::updateHealthConnect,
        updateExactAlarm = permissionViewModel::updateExactAlarm,
    )
}

@Composable
private fun PermissionScreen(
    context: Context = LocalContext.current,
    healthConnectPermissionContract: ActivityResultContract<Set<String>, Set<String>>,
    requireInstallHealthApk: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
    notificationPermission: Boolean,
    activityRecognitionPermission: Boolean,
    healthConnectPermission: Boolean,
    exactAlarmPermission: Boolean,
    updateNotification: (Boolean) -> Unit,
    updateActivityRecognition: (Boolean) -> Unit,
    updateHealthConnect: (Boolean) -> Unit,
    updateExactAlarm: (Boolean) -> Unit,
) {
    var dialogState by remember {
        mutableStateOf(
            DialogState.getInitValue().copy(
                header = "권한을 수락해 주세요.",
                positiveMessage = "설정 하러 가기",
                negativeMessage = "종료",
            )
        )
    }

    StepMateDialog(dialogState = dialogState) {
        dialogState = dialogState.copy(isShown = false)
    }

    var isRequestedExactAlarm by remember {
        mutableStateOf(Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
    }

    LifecycleStartEffect(key1 = Unit) {
        if (isRequestedExactAlarm && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            if (alarmManager.canScheduleExactAlarms())
                updateExactAlarm(true)
            else
                dialogState = dialogState.copy(
                    content = "알람 및 리마인더 권한이 수락되지 않았어요.\n 앱 설정 > 알람 및 리마인더 > 허용 으로 변경해 주세요.",
                    onPositiveCallback = {
                        context.requireAppSettings()
                    },
                    onNegativeCallback = {
                        dialogState = dialogState.copy(isShown = false)
                    },
                    isShown = true,
                )

            isRequestedExactAlarm = false
        }
        onStopOrDispose { }
    }

    DefaultLayout(
        modifier = Modifier,
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
                desc = "실시간 만보기 수치 표시, 미션 달성, 친구 신청, 공지 사항 등의 기능을 수신하기 위한 알림 권한이 필요해요.",
                permissionContract = ActivityResultContracts.RequestPermission(),
                onPermissionResult = { result ->
                    updateNotification(result)
                    if (!result)
                        dialogState = dialogState.copy(
                            content = "알림 권한이 수락되지 않았어요.\n 앱 설정 > 앱 권한 > 알림 권한 허용 으로 변경해 주세요.",
                            onPositiveCallback = {
                                context.requireAppSettings()
                            },
                            onNegativeCallback = {
                                dialogState = dialogState.copy(isShown = false)
                            },
                            isShown = true,
                        )
                },
                permission = Manifest.permission.POST_NOTIFICATIONS,
                showSnackBar = showSnackBar,
            )
        VerticalSpacer(height = 40.dp)
        PermissionActivityResultDescription(
            buttonStatus = activityRecognitionPermission,
            headline = "신체 활동",
            desc = "만보기 기능을 이용하기 위해 실시간으로 스마트폰의 센서로 부터 걸음수를 수집하고 있어요.\n해당 권한을 수락해 주셔야 걸음수를 수집할 수 있어요.",
            permissionContract = ActivityResultContracts.RequestPermission(),
            onPermissionResult = { result ->
                updateActivityRecognition(result)
                if (!result)
                    dialogState = dialogState.copy(
                        content = "신체 활동 권한이 수락되지 않았어요.\n 앱 설정 > 앱 권한 > 신체 활동 권한 허용 으로 변경해 주세요.",
                        onPositiveCallback = {
                            context.requireAppSettings()
                        },
                        onNegativeCallback = {
                            dialogState = dialogState.copy(isShown = false)
                        },
                        isShown = true,
                    )
            },
            permission = Manifest.permission.ACTIVITY_RECOGNITION,
            showSnackBar = showSnackBar,
        )
        VerticalSpacer(height = 40.dp)
        PermissionActivityResultDescription(
            buttonStatus = healthConnectPermission,
            headline = "헬스 커넥트",
            desc = "수집한 걸음수를 저장하기 위해 헬스 커넥트에 읽고 쓸수 있는 권한이 필요해요.\nStepMate는 차후 걸음수 뿐만 아니라 다양한 헬스 데이터를 활용하여 더 좋은 경험을 제공해 드릴 예정이에요.",
            permissionContract = healthConnectPermissionContract,
            onPermissionResult = { result ->
                val isGranted = result.containsAll(
                    healthConnectPermissions
                )

                updateHealthConnect(
                    result.containsAll(
                        healthConnectPermissions
                    )
                )

                if (!isGranted)
                    dialogState = dialogState.copy(
                        content = "헬스 커넥트 권한이 수락되지 않았어요.\n 헬스 커넥트 설정 > 앱 권한 > StepMate > 걸음수 권한 허용 으로 변경해 주세요.",
                        isShown = true,
                        onPositiveCallback = {
                            requireInstallHealthApk()
                        },
                        onNegativeCallback = {
                            dialogState = dialogState.copy(isShown = false)
                        },
                    )
            },
            permission = healthConnectPermissions,
            showSnackBar = showSnackBar,
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

private fun Context.requireAppSettings() =
    startActivity(Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
        flags = Intent.FLAG_ACTIVITY_NEW_TASK
    })


@Composable
internal fun <I, O> PermissionActivityResultDescription(
    buttonStatus: Boolean,
    headline: String,
    desc: String,
    permissionContract: ActivityResultContract<I, O>,
    onPermissionResult: (O) -> Unit,
    permission: I,
    showSnackBar: (SnackBarMessage) -> Unit,
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
            permissionLauncher.launch(permission)
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
private fun PreviewPermissionScreen() = StepWalkTheme {
    PermissionScreen(
        healthConnectPermissionContract = PermissionController.createRequestPermissionResultContract(),
        showSnackBar = {},
        notificationPermission = true,
        activityRecognitionPermission = false,
        healthConnectPermission = false,
        exactAlarmPermission = true,
        updateHealthConnect = {},
        updateActivityRecognition = {},
        updateNotification = {},
        updateExactAlarm = {},
        requireInstallHealthApk = {},
    )
}