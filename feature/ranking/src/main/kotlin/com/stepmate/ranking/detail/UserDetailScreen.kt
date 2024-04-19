package com.stepmate.ranking.detail

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.stepmate.core.SnackBarMessage
import com.stepmate.core.getCharacter
import com.stepmate.design.component.DescriptionLargeText
import com.stepmate.design.component.FooterText
import com.stepmate.design.component.HorizontalSpacer
import com.stepmate.design.component.HorizontalWeightSpacer
import com.stepmate.design.component.StepMateProgressIndicatorRotating
import com.stepmate.design.component.StepMateTitleTopBar
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.design.component.clickableAvoidingDuplication
import com.stepmate.design.component.layout.DefaultLayout
import com.stepmate.design.component.layout.ExceptionScreen
import com.stepmate.design.component.layout.chart.PopUpState
import com.stepmate.design.component.layout.chart.addChartPopUpDismiss
import com.stepmate.design.theme.StepWalkColor
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.ranking.detail.component.MissionLatest
import com.stepmate.ranking.detail.component.UserDetailHealthChart
import com.stepmate.ranking.rank.component.RankNumber
import com.stepmate.ranking.rank.component.UserStepProgress

@Composable
internal fun UserDetailScreen(
    navigateToRanking: () -> Unit,
    userDetailViewModel: UserDetailViewModel = hiltViewModel(),
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
) {
    val uiState by userDetailViewModel.uiState.collectAsStateWithLifecycle()
    val snackBarMessage by userDetailViewModel.snackBarState.collectAsStateWithLifecycle(
        initialValue = SnackBarMessage.getInitValues()
    )
    val isFriend by userDetailViewModel.isFriendState.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = snackBarMessage) {
        if (snackBarMessage.headerMessage.isNotBlank())
            showSnackBar(snackBarMessage)
    }

    UserDetailScreen(
        uiState = uiState,
        isFriend = isFriend,
        originWasFriend = userDetailViewModel::isFriend.get(),
        popBackStack = popBackStack,
        navigateToRanking = navigateToRanking,
        addFriend = userDetailViewModel::manageFriendShip,
    )
}

@Composable
private fun UserDetailScreen(
    uiState: UserDetailViewModel.UiState,
    isFriend: Boolean,
    originWasFriend: Boolean,
    popBackStack: () -> Unit,
    navigateToRanking: () -> Unit,
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
                isFriend = isFriend,
                originWasFriend = originWasFriend,
                popBackStack = popBackStack,
                navigateToRanking = navigateToRanking,
                addFriend = addFriend,
            )
        }
    }
}

@Composable
internal fun OnSuccessUserDetailScreen(
    modifier: Modifier = Modifier,
    user: User,
    isFriend: Boolean,
    originWasFriend: Boolean,
    popBackStack: () -> Unit,
    navigateToRanking: () -> Unit,
    addFriend: () -> Unit,
) {
    var popUpState by remember {
        mutableStateOf(PopUpState.getInitValues())
    }

    BackHandler {
        if (originWasFriend && !isFriend) {
            navigateToRanking()
        } else
            popBackStack()
    }

    DefaultLayout(
        modifier = modifier.addChartPopUpDismiss(
            popUpState = popUpState,
            setPopUpState = { bool ->
                popUpState = popUpState.copy(enabled = bool)
            }
        ),
        topBar = {
            StepMateTitleTopBar(
                modifier = Modifier,
                icon = com.stepmate.design.R.drawable.ic_arrow_left_small,
                onClick = {
                    if (originWasFriend && !isFriend) {
                        navigateToRanking()
                    } else
                        popBackStack()
                },
                text = "개인정보"
            )
        }
    ) {
        val composition by rememberLottieComposition(LottieCompositionSpec.Asset(getCharacter(user.info.level)))
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
                    text = if (!isFriend) "친구 추가" else "친구 삭제",
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
@Preview
private fun PreviewUserDetailScreen(
    @PreviewParameter(UserDetailPreviewParameter::class)
    user: User,
) = StepMateTheme {
    UserDetailScreen(
        uiState = UserDetailViewModel.UiState.Success(
            user
        ),
        isFriend = false,
        originWasFriend = false,
        popBackStack = {},
        addFriend = {},
        navigateToRanking = {},
    )
}