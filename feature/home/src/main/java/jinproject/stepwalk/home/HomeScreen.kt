package jinproject.stepwalk.home

import android.content.Context
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.health.connect.client.PermissionController
import androidx.health.connect.client.permission.HealthPermission
import androidx.health.connect.client.records.HeartRateRecord
import androidx.health.connect.client.records.StepsRecord
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.design.component.DefaultLayout
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.domain.METs
import jinproject.stepwalk.home.component.HomeTopAppBar
import jinproject.stepwalk.home.component.UserPager
import jinproject.stepwalk.home.state.HeartRate
import jinproject.stepwalk.home.state.Step
import jinproject.stepwalk.home.state.Time
import java.time.Instant
import java.time.ZoneId
import java.time.temporal.ChronoUnit

private val PERMISSIONS =
    setOf(
        HealthPermission.getReadPermission(StepsRecord::class),
        HealthPermission.getWritePermission(StepsRecord::class),
        HealthPermission.getReadPermission(HeartRateRecord::class),
        HealthPermission.getWritePermission(HeartRateRecord::class)
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
    val selectedStepOnGraph by homeViewModel.selectedStepOnGraph.collectAsStateWithLifecycle()

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

                /*(0..23).forEach { count ->
                    healthConnector.insertHeartRates(
                        heartRate = (count % 4) * 40L,
                        startTime = instant.truncatedTo(ChronoUnit.DAYS).toInstant().plus(count.toLong(), ChronoUnit.HOURS),
                        endTime = instant.truncatedTo(ChronoUnit.DAYS).toInstant().plus(count.toLong(), ChronoUnit.HOURS).plus(30L,ChronoUnit.MINUTES)
                    )
                }*/

                homeViewModel::setSteps.invoke(
                    healthConnector.readStepsByTimeRange(
                        startTime = instant.truncatedTo(ChronoUnit.DAYS).toInstant(),
                        endTime = instant.withHour(23).toInstant(),
                        type = METs.Walk
                    ) ?: listOf(Step.getInitValues())
                )

                homeViewModel::setHeartRates.invoke(
                    healthConnector.readHeartRatesByTimeRange(
                        startTime = instant.truncatedTo(ChronoUnit.DAYS).toInstant(),
                        endTime = instant.withHour(23).toInstant()
                    ) ?: listOf(HeartRate.getInitValues())
                )

            } else {
                Log.d("test", "권한 없음")
                permissionLauncher.launch(PERMISSIONS)
            }
        }
    }

    HomeScreen(
        uiState = uiState,
        selectedStepOnGraph = selectedStepOnGraph,
        setSelectedStepOnGraph = homeViewModel::setSelectedStepOnGraph,
        setTimeOnGraph = homeViewModel::setTime
    )
}

@Composable
private fun HomeScreen(
    uiState: HomeUiState,
    selectedStepOnGraph: Long,
    setSelectedStepOnGraph: (Long) -> Unit,
    setTimeOnGraph: (Time) -> Unit
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
                onClickIcon1 = {},
                onClickIcon2 = {}
            )
        },
    ) {
        UserPager(
            uiState = uiState,
            selectedStepOnGraph = selectedStepOnGraph,
            setSelectedStepOnGraph = setSelectedStepOnGraph
        )
        PopUpWindow(
            popUpState = popUpState.value,
            offPopUp = { popUpState.value = false },
            onClickPopUpItem = { time -> setTimeOnGraph(time) }
        )
    }
}

@Composable
private fun PopUpWindow(
    popUpState: Boolean,
    offPopUp: () -> Unit,
    onClickPopUpItem: (Time) -> Unit
) {
    if (popUpState) {
        Popup(
            popupPositionProvider = object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize
                ): IntOffset {
                    return IntOffset(
                        x = 20,
                        y = 200
                    )
                }
            },
            properties = PopupProperties(),
            onDismissRequest = offPopUp
        ) {
            Column(
                modifier = Modifier
                    .width(50.dp)
                    .shadow(5.dp, RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Time.values().forEachIndexed { index, time ->
                    Text(
                        text = time.display(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.clickable {
                            onClickPopUpItem(time)
                        }
                    )
                    if(index != Time.values().lastIndex) {
                        VerticalSpacer(height = 4.dp)
                    }
                }
            }
        }
    }
}

@Composable
@Preview
private fun PreviewHomeScreen() = StepWalkTheme {
    HomeScreen(
        uiState = HomeUiState.getInitValues(),
        selectedStepOnGraph = 0L,
        setSelectedStepOnGraph = {},
        setTimeOnGraph = {}
    )
}
