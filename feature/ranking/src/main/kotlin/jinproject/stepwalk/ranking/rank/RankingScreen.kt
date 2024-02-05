package jinproject.stepwalk.ranking.rank

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.pullrefresh.PullRefreshIndicator
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import jinproject.stepwalk.core.SnackBarMessage
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.DialogState
import jinproject.stepwalk.design.component.FooterText
import jinproject.stepwalk.design.component.HorizontalSpacer
import jinproject.stepwalk.design.component.HorizontalWeightSpacer
import jinproject.stepwalk.design.component.StepMateDialog
import jinproject.stepwalk.design.component.StepMateProgressIndicatorRotating
import jinproject.stepwalk.design.component.StepMatePullRefreshIndicator
import jinproject.stepwalk.design.component.StepMatePushRefreshIndicator
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.component.clickableAvoidingDuplication
import jinproject.stepwalk.design.component.layout.HideableTopBarLayout
import jinproject.stepwalk.design.component.pushRefresh.pushRefresh
import jinproject.stepwalk.design.component.pushRefresh.rememberPushRefreshState
import jinproject.stepwalk.design.component.systembarhiding.SystemBarHidingState
import jinproject.stepwalk.design.component.systembarhiding.rememberSystemBarHidingState
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.ranking.detail.UserDetailPreviewParameter
import jinproject.stepwalk.ranking.rank.component.RankNumber
import jinproject.stepwalk.ranking.rank.component.RankTop3
import jinproject.stepwalk.ranking.rank.component.RankingTopBar
import jinproject.stepwalk.ranking.rank.component.UserCharacterWithStepProgress
import jinproject.stepwalk.ranking.rank.state.RankBoardPreviewParameter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun RankingScreen(
    rankingViewModel: RankingViewModel = hiltViewModel(),
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
    navigateToRankingUserDetail: (String, Int) -> Unit,
) {
    val user by rankingViewModel.user.collectAsStateWithLifecycle()
    val rankBoard by rankingViewModel.rankBoard.collectAsStateWithLifecycle()
    val uiState by rankingViewModel.uiState.collectAsStateWithLifecycle(
        initialValue = RankingViewModel.UiState.Loading,
    )

    RankingScreen(
        uiState = uiState,
        user = user,
        rankBoard = rankBoard,
        fetchRanking = rankingViewModel::fetchRanking,
        fetchMoreRankBoard = rankingViewModel::fetchMoreRankBoard,
        navigateToRankingUserDetail = navigateToRankingUserDetail,
    )
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
private fun RankingScreen(
    uiState: RankingViewModel.UiState,
    user: User,
    rankBoard: RankBoard,
    fetchRanking: (String) -> Unit,
    fetchMoreRankBoard: () -> Unit,
    navigateToRankingUserDetail: (String, Int) -> Unit,
) {
    when (uiState) {
        is RankingViewModel.UiState.Error -> {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize()
            ) {
                DescriptionSmallText(text = uiState.exception.message.toString())
            }
        }

        RankingViewModel.UiState.Loading -> {
            StepMateProgressIndicatorRotating()
        }

        RankingViewModel.UiState.Success -> {
            val isPullRefreshing by remember {
                mutableStateOf(false)
            }
            
            val pullRefreshState = rememberPullRefreshState(
                refreshing = isPullRefreshing,
                onRefresh = {
                    fetchRanking(user.info.name)
                }
            )

            Box(modifier = Modifier.pullRefresh(pullRefreshState)) {
                OnSuccessRankingScreen(
                    modifier = Modifier
                        .fillMaxSize()
                        .align(Alignment.Center),
                    user = user,
                    rankBoard = rankBoard,
                    fetchMoreRankBoard = fetchMoreRankBoard,
                    navigateToRankingUserDetail = navigateToRankingUserDetail,
                )
                PullRefreshIndicator(
                    modifier = Modifier.align(Alignment.TopCenter),
                    refreshing = isPullRefreshing,
                    state = pullRefreshState,
                )
            }
        }
    }
}

@Composable
internal fun OnSuccessRankingScreen(
    modifier: Modifier = Modifier,
    density: Density = LocalDensity.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    user: User,
    rankBoard: RankBoard,
    fetchMoreRankBoard: () -> Unit,
    navigateToRankingUserDetail: (String, Int) -> Unit,
) {
    val systemBarHidingState = rememberSystemBarHidingState(
        bar = SystemBarHidingState.Bar.TOPBAR(
            maxHeight = with(density) {
                val systemBarPadding = WindowInsets.systemBars.asPaddingValues()
                150.dp.roundToPx() + systemBarPadding.calculateTopPadding()
                    .roundToPx() + systemBarPadding.calculateBottomPadding().roundToPx()
            },
            minHeight = 0,
        )
    )

    var isRefreshing by remember {
        mutableStateOf(false)
    }

    val pushRefreshState = rememberPushRefreshState(
        onRefresh = {
            coroutineScope.launch {
                isRefreshing = true
                delay(1000)
                fetchMoreRankBoard()
                isRefreshing = false
            }
        },
        maxHeight = with(density) {
            50.dp.toPx()
        },
        isRefreshing = isRefreshing,
    )

    var dialogState by remember { mutableStateOf(DialogState.getInitValue()) }

    StepMateDialog(
        dialogState = dialogState,
        hideDialog = {
            dialogState = dialogState.copy(isShown = false)
        },
    )

    HideableTopBarLayout(
        modifier = modifier
            .pushRefresh(pushRefreshState = pushRefreshState),
        systemBarHidingState = systemBarHidingState,
        topBar = { topBarModifier ->
            RankingTopBar(
                modifier = topBarModifier,
                user = user,
            )
        }
    ) { contentModifier ->
        LazyColumn(
            modifier = contentModifier,
            contentPadding = PaddingValues(vertical = 4.dp),
        ) {
            item {
                RankTop3(
                    rankBoard = rankBoard,
                    dialogState = dialogState,
                    setDialogState = { dialog -> dialogState = dialog },
                    navigateToRankingUserDetail = navigateToRankingUserDetail,
                )
            }
            items(rankBoard.remain, key = { rank -> rank.name }) { rank ->
                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .clickableAvoidingDuplication {
                            navigateToRankingUserDetail(
                                rank.name,
                                rankBoard.highestStep,
                            )
                        }
                        .shadow(4.dp, clip = true, shape = RoundedCornerShape(20.dp))
                        .background(MaterialTheme.colorScheme.surface)
                        .padding(8.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.Bottom
                    ) {
                        FooterText(text = rank.designation)
                        HorizontalSpacer(width = 4.dp)
                        DescriptionSmallText(text = rank.name)
                        HorizontalWeightSpacer(float = 1f)
                        RankNumber(
                            rank = rank,
                        )
                    }
                    VerticalSpacer(height = 10.dp)
                    UserCharacterWithStepProgress(rank = rank, maxStep = rankBoard.highestStep)
                }
            }
            item {
                StepMatePushRefreshIndicator(
                    state = pushRefreshState,
                    isRefreshing = isRefreshing,
                    modifier = Modifier
                )
            }
        }
    }
}

@Composable
internal fun RankPopUp(
    popUpState: Boolean,
    offPopUp: () -> Unit,
    content: @Composable ColumnScope.() -> Unit,
) {
    val animState by animateFloatAsState(
        targetValue = if (popUpState) 1f else 0f,
        label = "PopUp Animation State",
        animationSpec = tween(300)
    )

    if (popUpState) {
        Popup(
            popupPositionProvider = object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize,
                ): IntOffset {
                    return IntOffset(
                        x = anchorBounds.width,
                        y = anchorBounds.top
                    )
                }
            },
            properties = PopupProperties(focusable = true),
            onDismissRequest = offPopUp
        ) {
            Column(
                modifier = Modifier
                    .graphicsLayer {
                        scaleX = animState
                        scaleY = animState
                        alpha = animState
                    }
                    .width(100.dp)
                    .shadow(5.dp, RoundedCornerShape(10.dp), true)
                    .background(MaterialTheme.colorScheme.surface)
                    .padding(8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                content()
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewRankingScreen(
    @PreviewParameter(RankBoardPreviewParameter::class)
    rankBoard: RankBoard,
) = StepWalkTheme {
    RankingScreen(
        uiState = RankingViewModel.UiState.Success,
        user = UserDetailPreviewParameter().values.first(),
        rankBoard = rankBoard,
        fetchRanking = {},
        fetchMoreRankBoard = {},
        navigateToRankingUserDetail = { _, _ -> }
    )
}