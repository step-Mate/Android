package com.stepmate.ranking.noti

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.stepmate.core.SnackBarMessage
import com.stepmate.design.R
import com.stepmate.design.appendBoldText
import com.stepmate.design.appendFontSizeWithColorText
import com.stepmate.design.component.DefaultIconButton
import com.stepmate.design.component.DescriptionAnnotatedSmallText
import com.stepmate.design.component.DescriptionSmallText
import com.stepmate.design.component.DialogState
import com.stepmate.design.component.HorizontalDividerItem
import com.stepmate.design.component.HorizontalSpacer
import com.stepmate.design.component.HorizontalWeightSpacer
import com.stepmate.design.component.StepMateDialog
import com.stepmate.design.component.StepMateTitleTopBar
import com.stepmate.design.component.layout.DefaultLayout
import com.stepmate.design.theme.StepWalkColor
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.design.tu
import com.stepmate.ranking.noti.state.RequestedFriendList

@Composable
internal fun RankNotificationScreen(
    rankNotiViewModel: RankNotiViewModel = hiltViewModel(),
    popBackStack: () -> Unit,
    showSnackBar: (SnackBarMessage) -> Unit,
    navigateToRanking: () -> Unit,
) {
    val requestedFriendList by rankNotiViewModel.requestedFriendList.collectAsStateWithLifecycle()
    val snackBarMessage by rankNotiViewModel.snackBarState.collectAsStateWithLifecycle(
        initialValue = SnackBarMessage.getInitValues()
    )

    LaunchedEffect(key1 = snackBarMessage) {
        if (snackBarMessage.headerMessage.isNotBlank())
            showSnackBar(snackBarMessage)
    }

    RankNotificationScreen(
        requestedFriendList = requestedFriendList,
        isNeedToRefresh = rankNotiViewModel::isNeedToRefresh.get(),
        processRequestFriend = rankNotiViewModel::processRequestFriend,
        popBackStack = popBackStack,
        navigateToRanking = navigateToRanking,
    )
}


@Composable
private fun RankNotificationScreen(
    requestedFriendList: RequestedFriendList,
    isNeedToRefresh: Boolean,
    processRequestFriend: (Boolean, String) -> Unit,
    popBackStack: () -> Unit,
    navigateToRanking: () -> Unit,
) {
    var dialogState by remember { mutableStateOf(DialogState.getInitValue()) }

    StepMateDialog(
        dialogState = dialogState,
        hideDialog = {
            dialogState = dialogState.copy(isShown = false)
        },
    )

    BackHandler {
        if (isNeedToRefresh)
            navigateToRanking()
        else
            popBackStack()
    }

    DefaultLayout(
        modifier = Modifier,
        topBar = {
            StepMateTitleTopBar(
                modifier = Modifier,
                icon = R.drawable.ic_arrow_left_small,
                onClick = {
                    if (isNeedToRefresh)
                        navigateToRanking()
                    else
                        popBackStack()
                },
                text = "받은 친구 신청"
            )
        },
        contentPaddingValues = PaddingValues(0.dp),
    ) {
        val dividerColor = MaterialTheme.colorScheme.outlineVariant

        if (requestedFriendList.list.isEmpty())
            DescriptionSmallText(
                text = "요청 받은 친구 신청이 없어요.",
                modifier = Modifier
                    .fillMaxSize()
                    .wrapContentSize(),
            )
        else
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                itemsIndexed(requestedFriendList.list) { idx, user ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp, horizontal = 16.dp),
                    ) {
                        DescriptionAnnotatedSmallText(
                            text = buildAnnotatedString {
                                appendBoldText(user)
                                appendFontSizeWithColorText(
                                    text = " 님이 친구 요청을 하셨어요.\n\n수락하시겠어요?",
                                    color = MaterialTheme.colorScheme.onSurface,
                                    fontSize = 10.tu,
                                )
                            }
                        )

                        HorizontalWeightSpacer(float = 1f)

                        DefaultIconButton(
                            icon = R.drawable.ic_check,
                            onClick = { processRequestFriend(true, user) },
                            iconTint = StepWalkColor.blue_400.color,
                            modifier = Modifier
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.surfaceVariant,
                                    RoundedCornerShape(20.dp)
                                ),
                        )
                        HorizontalSpacer(width = 8.dp)
                        DefaultIconButton(
                            icon = R.drawable.ic_x,
                            onClick = { processRequestFriend(false, user) },
                            iconTint = StepWalkColor.red_400.color,
                            modifier = Modifier.border(
                                1.dp,
                                MaterialTheme.colorScheme.surfaceVariant,
                                RoundedCornerShape(20.dp)
                            ),
                        )
                    }

                    HorizontalDividerItem(
                        color = dividerColor,
                    )
                }
            }
    }
}

@Composable
@Preview
private fun PreviewRankNotificationScreen() = StepMateTheme {
    RankNotificationScreen(
        requestedFriendList = RequestedFriendList(listOf("홍길동", "박민영", "냐옹이")),
        processRequestFriend = { _, _ -> },
        isNeedToRefresh = false,
        popBackStack = {},
        navigateToRanking = {},
    )
}