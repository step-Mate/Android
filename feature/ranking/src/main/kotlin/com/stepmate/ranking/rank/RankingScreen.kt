package com.stepmate.ranking.rank

import android.util.Log
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleStartEffect
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavOptions
import com.stepmate.core.SnackBarMessage
import com.stepmate.design.component.DefaultButton
import com.stepmate.design.component.DescriptionSmallText
import com.stepmate.design.component.DialogState
import com.stepmate.design.component.StepMateDialog
import com.stepmate.design.component.StepMateProgressIndicatorRotating
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.design.component.layout.ExceptionScreen
import com.stepmate.design.component.layout.HideableTopBarLayout
import com.stepmate.design.component.pushRefresh.pushRefresh
import com.stepmate.design.component.pushRefresh.rememberPushRefreshState
import com.stepmate.design.component.systembarhiding.SystemBarHidingState
import com.stepmate.design.component.systembarhiding.rememberSystemBarHidingState
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.ranking.detail.UserDetailPreviewParameter
import com.stepmate.ranking.rank.component.RankBoard
import com.stepmate.ranking.rank.component.RankingTopBar
import com.stepmate.ranking.rank.state.RankBoardPreviewParameter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
internal fun RankingScreen(
    rankingViewModel: RankingViewModel = hiltViewModel(),
    showSnackBar: (SnackBarMessage) -> Unit,
    navigateToRankingUserDetail: (String, Int, Boolean) -> Unit,
    navigateToLogin: (NavOptions?) -> Unit,
    navigateToNoti: () -> Unit,
) {
    val user by rankingViewModel.user.collectAsStateWithLifecycle()
    val rankBoard by rankingViewModel.rankBoard.collectAsStateWithLifecycle()
    val friendRankBoard by rankingViewModel.friendRankBoard.collectAsStateWithLifecycle()
    val uiState by rankingViewModel.uiState.collectAsStateWithLifecycle(
        initialValue = RankingViewModel.UiState.Loading,
    )
    val snackBarMessage by rankingViewModel.snackBarState.collectAsStateWithLifecycle(
        initialValue = SnackBarMessage.getInitValues(),
        minActiveState = Lifecycle.State.CREATED,
    )
    val isRequestedFriend by rankingViewModel.isRequestedFriend.collectAsStateWithLifecycle()

    LaunchedEffect(key1 = snackBarMessage) {
        if (snackBarMessage.headerMessage.isNotBlank())
            showSnackBar(snackBarMessage)
    }

    LifecycleStartEffect {
        rankingViewModel::deleteFriendIfDeleted.invoke()

        onStopOrDispose {}
    }

    RankingScreen(
        uiState = uiState,
        user = user,
        rankBoard = rankBoard,
        friendRankBoard = friendRankBoard,
        fetchMoreMonthRankBoard = rankingViewModel::fetchMoreMonthRankBoard,
        changeRankTab = rankingViewModel::changeRankTab,
        navigateToRankingUserDetail = navigateToRankingUserDetail,
        navigateToLogin = navigateToLogin,
        navigateToNoti = navigateToNoti,
        isRequestedFriend = isRequestedFriend,
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun RankingScreen(
    uiState: RankingViewModel.UiState,
    user: RankingViewModel.User,
    rankBoard: RankBoard,
    friendRankBoard: RankBoard,
    fetchMoreMonthRankBoard: () -> Unit,
    changeRankTab: (RankingViewModel.RankTab) -> Unit,
    navigateToRankingUserDetail: (String, Int, Boolean) -> Unit,
    navigateToLogin: (NavOptions?) -> Unit,
    navigateToNoti: () -> Unit,
    isRequestedFriend: Boolean,
) {
    val pagerState = rememberPagerState(
        initialPage = 0,
        initialPageOffsetFraction = 0f,
        pageCount = { RankingViewModel.RankTab.entries.size }
    )

    when (uiState) {
        is RankingViewModel.UiState.Error -> {
            val exception = uiState.exception
            if (exception == RankingViewModel.CANNOT_LOGIN_EXCEPTION && exception.message == RankingViewModel.CANNOT_LOGIN_EXCEPTION.message) {
                ExceptionScreen(
                    headlineMessage = "로그인을 하실 수 없어요.",
                    causeMessage = "로그인을 하셔야 랭킹 기능을 이용하실 수 있어요.",
                    content = {
                        VerticalSpacer(height = 20.dp)
                        DefaultButton(onClick = {
                            navigateToLogin(null)
                        }) {
                            DescriptionSmallText(
                                text = "로그인 하러 가기",
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }
                )
            } else
                ExceptionScreen(
                    headlineMessage = "랭킹 기능을 이용할 수 없어요.",
                    causeMessage = uiState.exception.message.toString(),
                )
        }

        RankingViewModel.UiState.Loading -> {
            StepMateProgressIndicatorRotating()
        }

        is RankingViewModel.UiState.Success -> {
            OnSuccessRankingScreen(
                modifier = Modifier
                    .fillMaxSize(),
                user = user,
                rankBoard = rankBoard,
                friendRankBoard = friendRankBoard,
                fetchMoreMonthRankBoard = fetchMoreMonthRankBoard,
                pagerState = pagerState,
                changeRankTab = changeRankTab,
                navigateToRankingUserDetail = navigateToRankingUserDetail,
                navigateToNoti = navigateToNoti,
                isRequestedFriend = isRequestedFriend,
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
internal fun OnSuccessRankingScreen(
    modifier: Modifier = Modifier,
    density: Density = LocalDensity.current,
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    user: RankingViewModel.User,
    rankBoard: RankBoard,
    friendRankBoard: RankBoard,
    fetchMoreMonthRankBoard: () -> Unit,
    pagerState: PagerState,
    changeRankTab: (RankingViewModel.RankTab) -> Unit,
    navigateToRankingUserDetail: (String, Int, Boolean) -> Unit,
    navigateToNoti: () -> Unit,
    isRequestedFriend: Boolean,
) {
    val systemBarPadding = WindowInsets.systemBars.asPaddingValues()
    val topBarHeight = with(density) {
        (182.dp + systemBarPadding.calculateTopPadding()
                + systemBarPadding.calculateBottomPadding()).roundToPx()
    }
    val systemBarHidingState = rememberSystemBarHidingState(
        bar = SystemBarHidingState.Bar.TOPBAR(
            maxHeight = topBarHeight,
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
                fetchMoreMonthRankBoard()
                isRefreshing = false
            }
        },
        isRefreshing = isRefreshing,
    )

    val rankTabs = remember {
        RankingViewModel.RankTab.entries.toList()
    }

    LaunchedEffect(key1 = pagerState.currentPage) {
        changeRankTab(rankTabs[pagerState.currentPage])
    }

    var dialogState by remember { mutableStateOf(DialogState.getInitValue()) }

    StepMateDialog(
        dialogState = dialogState,
        hideDialog = {
            dialogState = dialogState.copy(isShown = false)
        },
    )

    val windowInsetsPadding = animateDpAsState(
        targetValue = if (systemBarHidingState.progress >= 0.95f)
            WindowInsets.systemBars.asPaddingValues().calculateTopPadding()
        else
            1.dp,
        label = "windowInsetsPadding",
    )

    HideableTopBarLayout(
        modifier = modifier,
        systemBarHidingState = systemBarHidingState,
        topBar = { topBarModifier ->
            RankingTopBar(
                modifier = topBarModifier,
                user = user.rank,
                maxStep = user.maxStep,
                navigateToNoti = navigateToNoti,
                isRequestedFriend = isRequestedFriend,
            )
        }
    ) { contentModifier ->

        Column(
            modifier = contentModifier.padding(top = windowInsetsPadding.value),
        ) {
            TabRow(
                modifier = Modifier
                    .height(22.dp),
                selectedTabIndex = pagerState.currentPage,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier
                            .tabIndicatorOffset(
                                tabPositions[pagerState.currentPage]
                            )
                            .padding(horizontal = 12.dp),
                        height = 2.dp,
                        color = MaterialTheme.colorScheme.primary
                    )
                },
                containerColor = MaterialTheme.colorScheme.surface
            ) {
                repeat(2) { page ->
                    Tab(
                        selected = pagerState.currentPage == page,
                        onClick = {
                            coroutineScope.launch {
                                pagerState.animateScrollToPage(page)
                                changeRankTab(rankTabs[page])
                            }
                        },
                        interactionSource = remember { MutableInteractionSource() }
                    ) {
                        DescriptionSmallText(
                            modifier = Modifier.padding(vertical = 8.dp),
                            text = rankTabs[page].display,
                        )
                    }
                }
            }

            HorizontalPager(
                modifier = Modifier.pushRefresh(pushRefreshState = pushRefreshState),
                state = pagerState,
            ) { page ->
                val currentTab = rankTabs[page]

                RankBoard(
                    title = currentTab.display,
                    rankBoard = when (currentTab) {
                        RankingViewModel.RankTab.MONTH -> rankBoard
                        RankingViewModel.RankTab.FRIEND -> friendRankBoard
                    },
                    dialogState = dialogState,
                    setDialogState = { dialog -> dialogState = dialog },
                    pushRefreshState = pushRefreshState,
                    isRefreshing = isRefreshing,
                    navigateToRankingUserDetail = { userName, maxStep ->
                        val isFriend = when (currentTab) {
                            RankingViewModel.RankTab.MONTH -> false
                            RankingViewModel.RankTab.FRIEND -> {
                                friendRankBoard.rankList.find { friends -> friends.name == userName }
                                    ?.let {
                                        true
                                    } ?: false
                            }
                        }

                        navigateToRankingUserDetail(userName, maxStep, isFriend)
                    },
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
) = StepMateTheme {
    RankingScreen(
        uiState = RankingViewModel.UiState.Success,
        user = RankingViewModel.User(
            rank = UserDetailPreviewParameter().values.first().info,
            maxStep = UserDetailPreviewParameter().values.first().maxStep,
        ),
        rankBoard = rankBoard,
        friendRankBoard = rankBoard,
        fetchMoreMonthRankBoard = {},
        changeRankTab = {},
        navigateToRankingUserDetail = { _, _, _ -> },
        navigateToLogin = {},
        navigateToNoti = {},
        isRequestedFriend = true,
    )
}