package jinproject.stepwalk.app.ui.home

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import jinproject.stepwalk.app.ui.home.component.UserPager
import jinproject.stepwalk.app.ui.home.state.HealthState
import jinproject.stepwalk.design.theme.StepWalkTheme
import java.time.Instant
import java.time.temporal.ChronoUnit

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
    val steps = rememberSaveable {
        mutableLongStateOf(0L)
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
                steps.longValue = healthConnector.readStepsByTimeRange(
                    startTime = Instant.now().minus(30,ChronoUnit.DAYS),
                    endTime = Instant.now()
                ) ?: 0L
            } else {
                Log.d("test","권한 없음")
                permissionLauncher.launch((PERMISSIONS).toTypedArray())
            }
        }
    }

    HomeScreen(
        steps = steps.longValue
    )
}

@Composable
private fun HomeScreen(steps: Long) {
    UserPager(
        pages = listOf(
            HealthState(
                name = "걷기",
                figure = 2000,
                max = 5000
            ),
            HealthState(
                name = "심박수",
                figure = 100,
                max = 200
            ),
            HealthState(
                name = "물 섭취량",
                figure = 2500,
                max = 2000
            ),
            HealthState(
                name = "산소포화도",
                figure = 10,
                max = 100
            )
        )
    )
}

@Composable
@Preview
private fun PreviewHomeScreen() = StepWalkTheme {
    HomeScreen(
        steps = 100L
    )
}
