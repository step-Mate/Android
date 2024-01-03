package jinproject.stepwalk.home.screen

import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.HealthConnector
import jinproject.stepwalk.home.screen.component.HomeTopAppBar
import jinproject.stepwalk.home.screen.component.HomePopUp
import jinproject.stepwalk.home.screen.component.page.UserPager
import jinproject.stepwalk.home.screen.state.Day
import jinproject.stepwalk.home.screen.state.SnackBarMessage
import jinproject.stepwalk.home.screen.state.Time
import jinproject.stepwalk.home.service.StepService
import jinproject.stepwalk.home.utils.onKorea
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

@Composable
internal fun HomeScreen(
    context: Context = LocalContext.current,
    homeViewModel: HomeViewModel = hiltViewModel(),
    navigateToCalendar: (Long) -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    val permissionState = rememberSaveable { mutableStateOf(false) }

    val permissionLauncher =
        rememberLauncherForActivityResult(contract = homeViewModel::permissionLauncher.get()) { result ->
            if (result.containsAll(homeViewModel::permissions.get())) {
                Log.d("test", "권한 수락 ${result.toString()} ")
                permissionState.value = true
            } else {
                showSnackBar(
                    SnackBarMessage(
                        headerMessage = "권한이 거부되었어요.",
                        contentMessage = "권한을 수락하시지 않으면 서비스를 이용할 수 없어요."
                    )
                )
                permissionState.value = false
            }
        }

    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val time by homeViewModel.time.collectAsStateWithLifecycle()

    LaunchedEffect(time, permissionState.value) {
        if (homeViewModel::checkPermissions.invoke()) {
            Log.d("test", "권한 있음")
            permissionState.value = true

            val instant = Instant.now().onKorea()

            /*(0..23).forEach { count ->
                healthConnector.insertSteps(
                    step = count * 100L + 100L,
                    startTime = instant.truncatedTo(ChronoUnit.DAYS).toInstant().plus(count.toLong(), ChronoUnit.HOURS),
                    endTime = instant.truncatedTo(ChronoUnit.DAYS).toInstant().plus(count.toLong(), ChronoUnit.HOURS).plus(30L,ChronoUnit.MINUTES)
                )
            }

            (0..23).forEach { count ->
                healthConnector.insertHeartRates(
                    heartRate = (count % 4) * 40L,
                    startTime = instant.truncatedTo(ChronoUnit.DAYS).toInstant().plus(count.toLong(), ChronoUnit.HOURS),
                    endTime = instant.truncatedTo(ChronoUnit.DAYS).toInstant().plus(count.toLong(), ChronoUnit.HOURS).plus(30L,ChronoUnit.MINUTES)
                )
            }*/

            when (time) {
                Day -> {
                    homeViewModel::setDurationHealthData.invoke(Duration.ofHours(1L))
                }

                else -> {
                    homeViewModel::setPeriodHealthData.invoke()
                }
            }

            if (permissionState.value)
                context.startForegroundService(Intent(context, StepService::class.java))
        } else {
            Log.d("test", "권한 없음")
            permissionLauncher.launch(homeViewModel::permissions.get())
        }
    }

    HomeScreen(
        uiState = uiState,
        setTimeOnGraph = homeViewModel::setTime,
        navigateToCalendar = navigateToCalendar
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    context: Context = LocalContext.current,
    setTimeOnGraph: (Time) -> Unit,
    navigateToCalendar: (Long) -> Unit,
) {
    val popUpState = remember {
        mutableStateOf(false)
    }

    DefaultLayout(
        modifier = Modifier,
        contentPaddingValues = PaddingValues(horizontal = 8.dp, vertical = 10.dp),
        topBar = {
            HomeTopAppBar(
                modifier = Modifier,
                onClickTimeIcon = { popUpState.value = true },
                onClickIcon1 = {
                    val firstInstallTime = context.packageManager.getPackageInfo(
                        context.packageName,
                        0
                    ).firstInstallTime
                    navigateToCalendar(
                        Instant.ofEpochMilli(firstInstallTime)
                            .minus(30L, ChronoUnit.DAYS).epochSecond
                    )
                },
                onClickIcon2 = {}
            )
        },
    ) {
        UserPager(
            modifier = Modifier.fillMaxSize(),
            uiState = uiState,
        )
        HomePopUp(
            popUpState = popUpState.value,
            offPopUp = { popUpState.value = false },
            onClickPopUpItem = { time -> setTimeOnGraph(time) }
        )
    }
}

@Composable
@Preview
private fun PreviewHomeScreen(
    @PreviewParameter(HomeUiStatePreviewParameters::class)
    homeUiState: HomeUiState
) = StepWalkTheme {
    HomeScreen(
        uiState = homeUiState,
        setTimeOnGraph = {},
        navigateToCalendar = {}
    )
}
