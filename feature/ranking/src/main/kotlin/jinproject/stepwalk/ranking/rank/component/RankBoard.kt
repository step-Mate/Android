package jinproject.stepwalk.ranking.rank.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.component.DefaultIconButton
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.DialogState
import jinproject.stepwalk.design.component.FooterText
import jinproject.stepwalk.design.component.HorizontalSpacer
import jinproject.stepwalk.design.component.HorizontalWeightSpacer
import jinproject.stepwalk.design.component.StepMatePushRefreshIndicator
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.component.clickableAvoidingDuplication
import jinproject.stepwalk.design.component.lazyList.TimeScheduler
import jinproject.stepwalk.design.component.lazyList.VerticalScrollBar
import jinproject.stepwalk.design.component.lazyList.addScrollBarNestedScrollConnection
import jinproject.stepwalk.design.component.lazyList.rememberScrollBarState
import jinproject.stepwalk.design.component.lazyList.rememberTimeScheduler
import jinproject.stepwalk.design.component.pushRefresh.PushRefreshState
import jinproject.stepwalk.design.component.pushRefresh.rememberPushRefreshState
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.ranking.rank.RankBoard
import jinproject.stepwalk.ranking.rank.state.RankBoardPreviewParameter
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch

@Composable
internal fun RankBoard(
    coroutineScope: CoroutineScope = rememberCoroutineScope(),
    lazyListState: LazyListState = rememberLazyListState(),
    density: Density = LocalDensity.current,
    title: String,
    rankBoard: RankBoard,
    dialogState: DialogState,
    setDialogState: (DialogState) -> Unit,
    pushRefreshState: PushRefreshState,
    isRefreshing: Boolean,
    timeScheduler: TimeScheduler = rememberTimeScheduler(),
    navigateToRankingUserDetail: (String, Int) -> Unit,
) {
    val isUpperScrollActive by remember {
        derivedStateOf {
            lazyListState.firstVisibleItemIndex > 3
        }
    }

    val top3RankHeight = 190.dp
    val rankBoardHeightPadding = 4.dp
    val viewPortSize by remember {
        derivedStateOf {
            lazyListState.layoutInfo.viewportSize
        }
    }

    val scrollBarState = rememberScrollBarState(
        maxHeight = with(density) {
            val rankItemHeight = (126.8).dp.toPx()
            //아이템 사이즈가 늘어날 때 마다 처음 보이는 화면의 크기만큼은 스크롤이 발생하면서 시작하기 때문에 차감해야 함
            val perViewPortHeight = viewPortSize.height * (rankBoard.page - 1)

            rankItemHeight * rankBoard.remain.size + top3RankHeight.toPx() - rankBoardHeightPadding.toPx() * 2 + perViewPortHeight
        }
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxWidth()
            .addScrollBarNestedScrollConnection(
                timer = timeScheduler,
                isUpperScrollActive = isUpperScrollActive,
                scrollBarState = scrollBarState,
            ),
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxHeight()
                .align(Alignment.Center),
            contentPadding = PaddingValues(vertical = 4.dp, horizontal = 16.dp),
            state = lazyListState,
        ) {
            item {
                RankTop3(
                    title = title,
                    rankBoard = rankBoard,
                    dialogState = dialogState,
                    setDialogState = setDialogState,
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
                        .padding(vertical = 8.dp, horizontal = 16.dp)
                ) {
                    // 전체 높이 112.dp + 높이 패딩 32.dp
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(14.dp),
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

        val upperScrollAlpha by animateFloatAsState(
            targetValue = if (timeScheduler.isRunning) 1f else 0f,
            label = "Alpha Animate State"
        )

        UpperScrollButton(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .graphicsLayer {
                    alpha = upperScrollAlpha
                    translationY = -50f
                }
                .shadow(1.dp, CircleShape),
            onClick = {
                coroutineScope.launch {
                    lazyListState.animateScrollToItem(0)
                    scrollBarState.changeOffset(0f)
                }
            },
        )

        if (isUpperScrollActive)
            VerticalScrollBar(
                scrollBarState = scrollBarState,
                lazyListState = lazyListState,
                headerItemHeight = top3RankHeight + rankBoardHeightPadding,
                perItemHeight = (144).dp,
            )

    }
}

@Composable
fun UpperScrollButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    DefaultIconButton(
        icon = R.drawable.ic_arrow_up_to_start,
        onClick = onClick,
        iconTint = MaterialTheme.colorScheme.onSurface,
        iconSize = 32.dp,
        modifier = modifier,
    )
}

@Composable
@Preview
private fun PreviewRankBoard(
    @PreviewParameter(RankBoardPreviewParameter::class)
    rankBoard: RankBoard,
) = StepWalkTheme {
    val isRefreshing = false

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        RankBoard(
            title = "월간 랭킹",
            rankBoard = rankBoard,
            dialogState = DialogState.getInitValue(),
            setDialogState = {},
            pushRefreshState = rememberPushRefreshState(
                onRefresh = {},
                isRefreshing = isRefreshing
            ),
            isRefreshing = isRefreshing,
            timeScheduler = rememberTimeScheduler().apply { setTime() },
            navigateToRankingUserDetail = { _, _ -> }
        )
    }
}