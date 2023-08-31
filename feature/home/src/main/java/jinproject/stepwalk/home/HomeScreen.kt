package jinproject.stepwalk.home

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.StepsRecord
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.home.component.HomeTopAppBar
import jinproject.stepwalk.home.component.UserPager
import jinproject.stepwalk.home.state.Step
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.domain.METs
import java.time.Instant
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneId
import java.time.temporal.ChronoField
import java.time.temporal.ChronoUnit

private val PERMISSIONS =
    setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class)
    )

@Composable
internal fun HomeScreen(
    context: Context = LocalContext.current,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val healthConnector = remember {
        HealthConnector(context)
    }

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = PermissionController.createRequestPermissionResultContract()) { result ->
            if (PERMISSIONS.containsAll(result)) {
                Log.d("test", "권한 수락")
            } else {
                Log.d("test", "권한 거부")
            }
        }

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(Unit) {
        healthConnector.healthConnectClient?.let { client ->
            val granted = client.permissionController.getGrantedPermissions()
            if (granted.containsAll(PERMISSIONS)) {
                Log.d("test", "권한 있음")

                val instant = Instant.now().atZone(ZoneId.of("Asia/Seoul"))

                /*(0..23).forEach { count ->
                    healthConnector.insertSteps(
                        step = count * 100L + 100L,
                        startTime = instant.truncatedTo(ChronoUnit.DAYS).toInstant().plus(count.toLong(), ChronoUnit.HOURS),
                        endTime = instant.truncatedTo(ChronoUnit.DAYS).toInstant().plus(count.toLong(), ChronoUnit.HOURS).plus(30L,ChronoUnit.MINUTES)
                    )
                }*/

                homeViewModel::setSteps.invoke(
                    healthConnector.readStepsByTimeRange(
                        startTime = instant.truncatedTo(ChronoUnit.DAYS).toInstant(),
                        endTime = instant.withHour(23).toInstant(),
                        type = METs.Walk
                    ) ?: emptyList()
                )

            } else {
                Log.d("test", "권한 없음")
                permissionLauncher.launch(PERMISSIONS)
            }
        }
    }

    HomeScreen(
        uiState = uiState
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeUiState
) {
    DefaultLayout(
        modifier = Modifier,
        contentPaddingValues = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
        topBar = {
            HomeTopAppBar(
                modifier = Modifier,
                onBackClick = { },
                onClickIcon1 = {},
                onClickIcon2 = {}
            )
        },
    ) {
        UserPager(
            uiState = uiState
        )
    }
}

@Composable
@Preview
private fun PreviewHomeScreen() = StepWalkTheme {
    HomeScreen(
        uiState = HomeUiState.getInitValues()
    )
}
