package jinproject.stepwalk.app.ui.home

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.HealthConnectClient
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.health.connect.client.request.ReadRecordsRequest
import androidx.health.connect.client.time.TimeRangeFilter
import jinproject.stepwalk.app.ui.home.component.UserSteps
import jinproject.stepwalk.design.theme.StepWalkTheme
import java.time.Instant
import java.time.ZoneOffset
import java.time.temporal.ChronoUnit
import javax.inject.Inject

private val PERMISSIONS =
    setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class)
    )

@Composable
fun HomeScreen(
    context: Context = LocalContext.current
) {
    val healthConnector = remember {
        HealthConnector(context)
    }
    val steps = remember {
        mutableStateOf(0L)
    }
    val permissionLauncher = rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { result ->
        if(result.all { permission -> permission.value }) {
            Log.d("test","권한 수락")
        } else {
            Log.d("test","권한 거부")
        }
    }

    LaunchedEffect(key1 = healthConnector) {
        healthConnector.healthConnectClient?.let { client ->
            val granted = client.permissionController.getGrantedPermissions()
            if (granted.containsAll(PERMISSIONS)) {
                Log.d("test","권한 있음")
                healthConnector.insertSteps()
                healthConnector.readStepsByTimeRange(
                    startTime = Instant.now().minus(30,ChronoUnit.DAYS),
                    endTime = Instant.now(),
                    changeStep = {step -> steps.value = step}
                )
            } else {
                Log.d("test","권한 없음")
                permissionLauncher.launch((PERMISSIONS).toTypedArray())
            }
        }
    }

    HomeScreen(
        steps = steps.value
    )
}

@Composable
private fun HomeScreen(steps: Long) {
    UserSteps(step = steps)
}

@Composable
@Preview
private fun PreviewHomeScreen() = StepWalkTheme {
    HomeScreen(
        steps = 100L
    )
}
