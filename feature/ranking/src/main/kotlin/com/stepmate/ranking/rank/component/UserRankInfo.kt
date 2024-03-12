package com.stepmate.ranking.rank.component

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
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
import com.stepmate.design.component.FooterText
import com.stepmate.design.component.HorizontalSpacer
import com.stepmate.design.component.layout.StepFooterLayout
import com.stepmate.design.component.layout.StepLayout
import com.stepmate.design.component.layout.StepProgress
import com.stepmate.design.theme.StepWalkColor
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.ranking.rank.Rank
import com.stepmate.ranking.rank.RankBoard
import com.stepmate.ranking.rank.state.RankBoardPreviewParameter
import kotlin.math.abs

@Composable
internal fun ColumnScope.UserCharacterWithStepProgress(
    rank: Rank,
    maxStep: Int,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(getCharacter(rank.level)))
    val lottieProgress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    val progress = if(rank.step == 0 || maxStep == 0) 0f else rank.step.toFloat() / maxStep

    StepLayout(
        modifier = Modifier.fillMaxWidth(),
        characterContent = {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                FooterText(
                    text = "Lv.${rank.level}",
                    color = StepWalkColor.blue_300.color,
                )
                LottieAnimation(
                    composition = composition,
                    progress = { lottieProgress },
                    modifier = Modifier.size(48.dp)
                )
            }
        },
        progressContent = { p ->
            StepProgress(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                progress = p
            )
        },
        progress = progress
    )
    StepFooterLayout(
        totalContent = {
            FooterText(
                text = rank.step.toString(),
            )
        },
        goalContent = {
            FooterText(text = maxStep.toString())
        },
        separatorContent = {
            FooterText(text = " / ")
        },
        progress = progress,
    )
}

@Composable
internal fun ColumnScope.UserStepProgress(
    rank: Rank,
    maxStep: Int,
) {
    val progress = if(rank.step == 0 || maxStep == 0) 0f else (rank.step.toFloat() / maxStep).coerceAtMost(1f)

    StepLayout(
        modifier = Modifier.fillMaxWidth(),
        characterContent = {
            Spacer(modifier = Modifier)
        },
        progressContent = { p ->
            StepProgress(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(20.dp),
                progress = p
            )
        },
        progress = progress
    )
    StepFooterLayout(
        totalContent = {
            FooterText(
                text = rank.step.toString(),
            )
        },
        goalContent = {
            FooterText(text = maxStep.toString())
        },
        separatorContent = {
            FooterText(text = " / ")
        },
        progress = progress,
    )
}

@Composable
internal fun RowScope.RankNumber(
    rank: Rank,
) {
    FooterText(text = "${rank.rankNumber}위")
    HorizontalSpacer(width = 4.dp)
    Icon(
        painter = painterResource(id = R.drawable.ic_triangle),
        contentDescription = "Changed Ranking Icon",
        tint = if (rank.dailyIncreasedRank < 0)
            StepWalkColor.blue_400.color
        else
            StepWalkColor.red_400.color,
        modifier = Modifier
            .size(8.dp)
            .then(
                if (rank.dailyIncreasedRank < 0) Modifier.rotate(180f)
                else Modifier
            )
    )
    HorizontalSpacer(width = 1.dp)
    FooterText(text = "${abs(rank.dailyIncreasedRank)}")
}

@Composable
@Preview(showBackground = true)
private fun PreviewUserCharacterWithStepProgress(
    @PreviewParameter(RankBoardPreviewParameter::class)
    rankBoard: RankBoard,
) = StepMateTheme {
    Column {
        UserCharacterWithStepProgress(
            rank = rankBoard.rankList.first(),
            maxStep = rankBoard.highestStep,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewUserStepProgress(
    @PreviewParameter(RankBoardPreviewParameter::class)
    rankBoard: RankBoard,
) = StepMateTheme {
    Column {
        UserStepProgress(
            rank = rankBoard.rankList.first(),
            maxStep = rankBoard.highestStep,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewRankNumber(
    @PreviewParameter(RankBoardPreviewParameter::class)
    rankBoard: RankBoard,
) = StepMateTheme {
    Row {
        RankNumber(
            rank = rankBoard.rankList.first(),
        )
    }
}