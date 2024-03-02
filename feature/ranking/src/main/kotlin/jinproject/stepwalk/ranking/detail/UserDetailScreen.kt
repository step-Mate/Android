package jinproject.stepwalk.ranking.detail

import android.content.res.Resources
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.design.component.DefaultButton
import jinproject.stepwalk.design.component.DefaultTextButton
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.FooterText
import jinproject.stepwalk.design.component.HorizontalSpacer
import jinproject.stepwalk.design.component.HorizontalWeightSpacer
import jinproject.stepwalk.design.component.StepMateProgressIndicatorRotating
import jinproject.stepwalk.design.component.StepMateTitleTopBar
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.component.clickableAvoidingDuplication
import jinproject.stepwalk.design.component.layout.DefaultLayout
import jinproject.stepwalk.design.component.layout.ExceptionScreen
import jinproject.stepwalk.design.component.layout.chart.PopUpState
import jinproject.stepwalk.design.component.layout.chart.addChartPopUpDismiss
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.domain.model.mission.MissionComponent
import jinproject.stepwalk.ranking.detail.component.UserDetailHealthChart
import jinproject.stepwalk.ranking.rank.component.RankNumber
import jinproject.stepwalk.ranking.rank.component.UserStepProgress

@Composable
internal fun UserDetailScreen(
    userDetailViewModel: UserDetailViewModel = hiltViewModel(),
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    val uiState by userDetailViewModel.uiState.collectAsStateWithLifecycle()
    val snackBarMessage by userDetailViewModel.snackBarState.collectAsStateWithLifecycle(
        initialValue = SnackBarMessage.getInitValues()
    )

    LaunchedEffect(key1 = snackBarMessage) {
        if (snackBarMessage.headerMessage.isNotBlank())
            showSnackBar(snackBarMessage)
    }

    UserDetailScreen(
        uiState = uiState,
        popBackStack = popBackStack,
        addFriend = userDetailViewModel::addFriend,
    )
}

@Composable
private fun UserDetailScreen(
    uiState: UserDetailViewModel.UiState,
    popBackStack: () -> Unit,
    addFriend: () -> Unit,
) {
    when (uiState) {
        is UserDetailViewModel.UiState.Error -> {
            ExceptionScreen(
                headlineMessage = "일시적인 장애가 발생했어요.",
                causeMessage = uiState.exception.message.toString(),
            )
        }

        UserDetailViewModel.UiState.Loading -> {
            StepMateProgressIndicatorRotating()
        }

        is UserDetailViewModel.UiState.Success -> {
            OnSuccessUserDetailScreen(
                user = uiState.user,
                popBackStack = popBackStack,
                addFriend = addFriend,
            )
        }
    }
}

@Composable
internal fun OnSuccessUserDetailScreen(
    modifier: Modifier = Modifier,
    resources: Resources = LocalContext.current.resources,
    user: User,
    popBackStack: () -> Unit,
    addFriend: () -> Unit,
) {
    var popUpState by remember {
        mutableStateOf(PopUpState.getInitValues())
    }
    DefaultLayout(
        modifier = modifier.addChartPopUpDismiss(
            popUpState = popUpState,
            setPopUpState = { state ->
                popUpState = state
            }
        ),
        topBar = {
            StepMateTitleTopBar(
                modifier = Modifier,
                icon = jinproject.stepwalk.design.R.drawable.ic_arrow_left_small,
                onClick = popBackStack,
                text = "개인정보"
            )
        }
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset(user.info.character))
        val lottieProgress by animateLottieCompositionAsState(
            composition,
            iterations = LottieConstants.IterateForever
        )

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(100.dp)
                )
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            LottieAnimation(
                composition = composition,
                progress = { lottieProgress },
                modifier = Modifier
                    .size(72.dp)
            )
            FooterText(text = "Lv.${user.info.level}")
        }
        VerticalSpacer(height = 16.dp)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentSize()
                .background(
                    MaterialTheme.colorScheme.surface,
                    RoundedCornerShape(20.dp)
                )
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Row(
                modifier = Modifier,
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                FooterText(
                    text = user.info.designation,
                    modifier = Modifier.alignByBaseline(),
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                HorizontalSpacer(width = 4.dp)
                DescriptionLargeText(
                    text = user.info.name,
                    modifier = Modifier.alignByBaseline()
                )
                HorizontalSpacer(width = 4.dp)
                FooterText(
                    text = "친구 추가",
                    modifier = Modifier
                        .clickableAvoidingDuplication {
                            addFriend()
                        }
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(20.dp),
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                )
                HorizontalWeightSpacer(float = 1f)
                RankNumber(
                    rank = user.info,
                )
            }
            VerticalSpacer(height = 12.dp)
            UserStepProgress(
                rank = user.info,
                maxStep = user.maxStep,
            )
        }
        VerticalSpacer(height = 16.dp)
        UserDetailHealthChart(
            graph = user.steps,
            header = "걸음수",
            barColor = listOf(
                StepWalkColor.blue_700.color,
                StepWalkColor.blue_600.color,
                StepWalkColor.blue_500.color,
                StepWalkColor.blue_400.color,
                StepWalkColor.blue_300.color,
                StepWalkColor.blue_200.color,
            ),
            popUpState = popUpState,
            setPopUpState = { state -> popUpState = state },
        )
        VerticalSpacer(height = 16.dp)
        MissionLatest(missions = user.latestMissions)
    }
}

@Composable
internal fun MissionLatest(
    missions: List<MissionComponent>,
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .padding(10.dp),
    ) {
        DescriptionLargeText(
            text = "진행중인 미션",
            modifier = Modifier.padding(vertical = 10.dp)
        )
        missions.forEachIndexed { index, mission ->
            key(mission.getMissionDesignation()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            MaterialTheme.colorScheme.outline,
                            RoundedCornerShape(10.dp)
                        )
                        .padding(8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    DescriptionSmallText(text = mission.getMissionDesignation())
                    HorizontalWeightSpacer(float = 1f)
                    FooterText(text = "${mission.getMissionAchieved()} / ${mission.getMissionGoal()}")
                }
            }
            if (index != missions.lastIndex)
                VerticalSpacer(height = 8.dp)
        }
    }

}

@Composable
@Preview
private fun PreviewUserDetailScreen(
    @PreviewParameter(UserDetailPreviewParameter::class)
    user: User,
) = StepWalkTheme {
    UserDetailScreen(
        uiState = UserDetailViewModel.UiState.Success(
            user
        ),
        popBackStack = {},
        addFriend = {},
    )
}