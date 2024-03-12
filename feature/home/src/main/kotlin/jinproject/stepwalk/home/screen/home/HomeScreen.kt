package jinproject.stepwalk.home.screen.home

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.component.layout.HideableTopBarLayout
import jinproject.stepwalk.design.component.layout.chart.PopUpState
import jinproject.stepwalk.design.component.layout.chart.addChartPopUpDismiss
import jinproject.stepwalk.design.component.systembarhiding.SystemBarHidingState
import jinproject.stepwalk.design.component.systembarhiding.rememberSystemBarHidingState
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.home.component.HomePopUp
import jinproject.stepwalk.home.screen.home.component.HomeTopAppBar
import jinproject.stepwalk.home.screen.home.component.tab.HealthTabLayout
import jinproject.stepwalk.home.screen.home.component.userinfo.UserInfoLayout
import jinproject.stepwalk.home.screen.home.state.Day
import jinproject.stepwalk.home.screen.home.state.Time
import jinproject.stepwalk.home.screen.home.state.User
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
    navigateToHomeSetting: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    val uiState by homeViewModel.uiState.collectAsStateWithLifecycle()
    val time by homeViewModel.time.collectAsStateWithLifecycle()
    val user by homeViewModel.user.collectAsStateWithLifecycle()

    LifecycleEventEffect(event = Lifecycle.Event.ON_CREATE) {
        context.startForegroundService(Intent(context, StepService::class.java))
    }

    LaunchedEffect(time) {
        if (homeViewModel::checkPermissions.invoke()) {
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
            // 데이터 -범위 예외처리

            when (time) {
                Day -> {
                    homeViewModel::setDurationHealthData.invoke(Duration.ofHours(1L))
                }

                else -> {
                    homeViewModel::setPeriodHealthData.invoke()
                }
            }
        } else {
            showSnackBar(
                SnackBarMessage(
                    headerMessage = "권한을 승인해 주세요.",
                    contentMessage = "권한이 없으면 오류가 발생할 수 있어요."
                )
            )
        }
    }

    HomeScreen(
        uiState = uiState,
        user = user,
        setTimeOnGraph = homeViewModel::setTime,
        navigateToCalendar = navigateToCalendar,
        navigateToHomeSetting = navigateToHomeSetting,
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    user: User,
    context: Context = LocalContext.current,
    density: Density = LocalDensity.current,
    setTimeOnGraph: (Time) -> Unit,
    navigateToCalendar: (Long) -> Unit,
    navigateToHomeSetting: () -> Unit,
) {
    val homePopUp = remember {
        mutableStateOf(false)
    }

    val systemBarHidingState = rememberSystemBarHidingState(
        SystemBarHidingState.Bar.TOPBAR(
            maxHeight = with(density) {
                200.dp.roundToPx()
            },
            minHeight = with(density) {
                84.dp.roundToPx()
            }
        )
    )

    var chartPopUp by remember {
        mutableStateOf(PopUpState.getInitValues())
    }

    HideableTopBarLayout(
        modifier = Modifier
            .addChartPopUpDismiss(
                popUpState = chartPopUp,
                setPopUpState = { bool -> chartPopUp = chartPopUp.copy(enabled = bool) }
            ),
        systemBarHidingState = systemBarHidingState,
        topBar = { modifier ->
            HomeTopAppBar(
                modifier = modifier,
                onClickTime = { homePopUp.value = true },
                onClickSetting = navigateToHomeSetting,
                content = {
                    UserInfoLayout(
                        modifier = Modifier
                            .padding(bottom = 10.dp, start = 12.dp, end = 12.dp),
                        step = uiState.step,
                        user = user,
                    )
                }
            )
        }) { modifier ->
        Column(
            modifier = modifier
                .background(MaterialTheme.colorScheme.background)
                .verticalScroll(rememberScrollState())
        ) {
            VerticalSpacer(height = 10.dp)
            HealthTabLayout(
                healthTab = uiState.step,
                user = user,
                navigateToDetailChart = {
                    val firstInstallTime = context.packageManager.getPackageInfo(
                        context.packageName,
                        0
                    ).firstInstallTime
                    navigateToCalendar(
                        Instant.ofEpochMilli(firstInstallTime)
                            .minus(30L, ChronoUnit.DAYS).epochSecond
                    )
                },
                popUpState = chartPopUp,
                setPopUpState = { state -> chartPopUp = state }
            )
            HomePopUp(
                popUpState = homePopUp.value,
                offPopUp = { homePopUp.value = false },
                onClickPopUpItem = { time -> setTimeOnGraph(time) }
            )
            VerticalSpacer(height = 500.dp)
        }
    }
}

@Composable
@Preview
private fun PreviewHomeScreen(
    @PreviewParameter(HomeUiStatePreviewParameters::class)
    homeUiState: HomeUiState,
) = StepWalkTheme {
    HomeScreen(
        uiState = homeUiState,
        user = User.getInitValues(),
        setTimeOnGraph = {},
        navigateToCalendar = {},
        navigateToHomeSetting = {},
    )
}