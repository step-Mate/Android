package com.stepmate.ranking.rank.component

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.stepmate.core.getCharacter
import com.stepmate.design.R
import com.stepmate.design.component.DefaultIconButton
import com.stepmate.design.component.DescriptionLargeText
import com.stepmate.design.component.DescriptionSmallText
import com.stepmate.design.component.DialogState
import com.stepmate.design.component.FooterText
import com.stepmate.design.component.HorizontalSpacer
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.design.component.clickableAvoidingDuplication
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.design.theme.StepWalkColor
import com.stepmate.ranking.rank.Rank
import com.stepmate.ranking.rank.RankBoard
import com.stepmate.ranking.rank.state.RankBoardPreviewParameter
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

@Composable
internal fun RankTop3(
    title: String,
    rankBoard: RankBoard,
    dialogState: DialogState,
    setDialogState: (DialogState) -> Unit,
    navigateToRankingUserDetail: (String, Int) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surface, RoundedCornerShape(20.dp))
            .padding(8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .wrapContentWidth()
                .padding(horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            DescriptionLargeText(text = title)
            HorizontalSpacer(width = 4.dp)
            DefaultIconButton(
                modifier = Modifier
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.onSurfaceVariant,
                        RoundedCornerShape(20.dp)
                    ),
                icon = R.drawable.ic_question_mark,
                onClick = {
                    val today = LocalDateTime.now()
                    val firstDayOfNextMonth =
                        today.plusMonths(1L).with(TemporalAdjusters.firstDayOfMonth())
                    val d_Day = today.until(firstDayOfNextMonth, ChronoUnit.DAYS)

                    setDialogState(
                        DialogState(
                            header = "랭킹은 ${d_Day}일 뒤에 초기화 될 예정이에요.",
                            content = "랭킹은 매일 자정 업데이트 되며, 매월 1일 자정에 초기화 되요.",
                            positiveMessage = "닫기",
                            onPositiveCallback = {
                                setDialogState(dialogState.copy(isShown = false))
                            },
                            isShown = true
                        )
                    )
                },
                iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                iconSize = 12.dp
            )
        }

        VerticalSpacer(height = 20.dp)

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(134.dp),
            verticalAlignment = Alignment.Bottom,
        ) {
            if (rankBoard.top3.getOrNull(1) != null)
                Ranker(
                    modifier = Modifier.height(114.dp),
                    rank = rankBoard.top3[1],
                    maxStep = rankBoard.highestStep,
                    navigateToRankingUserDetail = navigateToRankingUserDetail,
                )
            if (rankBoard.top3.getOrNull(0) != null)
                Ranker(
                    modifier = Modifier.height(134.dp),
                    rank = rankBoard.top3[0],
                    maxStep = rankBoard.highestStep,
                    navigateToRankingUserDetail = navigateToRankingUserDetail,
                )
            if (rankBoard.top3.getOrNull(2) != null)
                Ranker(
                    modifier = Modifier.height(114.dp),
                    rank = rankBoard.top3[2],
                    maxStep = rankBoard.highestStep,
                    navigateToRankingUserDetail = navigateToRankingUserDetail,
                )
        }
    }
}

@Composable
private fun Ranker(
    modifier: Modifier = Modifier,
    rank: Rank,
    maxStep: Int,
    navigateToRankingUserDetail: (String, Int) -> Unit,
    configuration: Configuration = LocalConfiguration.current,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(getCharacter(rank.level)))
    val lottieProgress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    val itemWidth = ((configuration.screenWidthDp - 32) / 3).dp

    Column(
        modifier = modifier
            .width(itemWidth)
            .clickableAvoidingDuplication {
                navigateToRankingUserDetail(
                    rank.name,
                    maxStep,
                )
            },
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Row {
            RankNumber(
                rank = rank,
            )
        }
        VerticalSpacer(height = 2.dp)
        FooterText(
            text = "Lv.${rank.level}",
            color = StepWalkColor.blue_300.color,
        )
        LottieAnimation(
            composition = composition,
            progress = { lottieProgress },
            modifier = Modifier
                .size(48.dp)
        )
        VerticalSpacer(height = 4.dp)
        FooterText(
            text = rank.designation,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        VerticalSpacer(height = 4.dp)
        DescriptionSmallText(text = rank.name)
        VerticalSpacer(height = 4.dp)
        FooterText(text = rank.step.toString())
    }
}

@Composable
@Preview
private fun PreviewRankTop3(
    @PreviewParameter(RankBoardPreviewParameter::class)
    rankBoard: RankBoard,
) = StepMateTheme {
    Column(Modifier.padding(horizontal = 16.dp)) {
        RankTop3(
            title = "월간 랭킹",
            rankBoard = rankBoard,
            dialogState = DialogState.getInitValue(),
            setDialogState = {},
            navigateToRankingUserDetail = { _, _ -> },
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewRanker(
    @PreviewParameter(RankBoardPreviewParameter::class)
    rankBoard: RankBoard,
) = StepMateTheme {
    Ranker(
        rank = rankBoard.top3.first(),
        maxStep = rankBoard.highestStep,
        navigateToRankingUserDetail = { _, _ -> },
    )
}