package jinproject.stepwalk.ranking.rank.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.appendFontSizeWithColorText
import jinproject.stepwalk.design.component.DefaultIconButton
import jinproject.stepwalk.design.component.DescriptionAnnotatedSmallText
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.DialogState
import jinproject.stepwalk.design.component.FooterText
import jinproject.stepwalk.design.component.HorizontalSpacer
import jinproject.stepwalk.design.component.HorizontalWeightSpacer
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.component.clickableAvoidingDuplication
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.design.tu
import jinproject.stepwalk.ranking.rank.Rank
import jinproject.stepwalk.ranking.rank.RankBoard
import jinproject.stepwalk.ranking.rank.state.RankBoardPreviewParameter
import java.time.LocalDateTime
import java.time.temporal.ChronoUnit
import java.time.temporal.TemporalAdjusters

@Composable
internal fun RankTop3(
    rankBoard: RankBoard,
    dialogState: DialogState,
    setDialogState: (DialogState) -> Unit,
    navigateToRankingUserDetail: (String, Int) -> Unit,
) {
    Column(
        modifier = Modifier
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
            DescriptionLargeText(text = "월간 리그")
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
                            header = "월간 랭킹은 ${d_Day}일 뒤에 초기화 될 예정이에요.",
                            content = "매월 1일 오전 9시에 초기화가 이루어져요.",
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
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            HorizontalWeightSpacer(float = 1f)
            Ranker(
                modifier = Modifier.height(90.dp),
                rank = rankBoard.top3[1],
                maxStep = rankBoard.highestStep,
                navigateToRankingUserDetail = navigateToRankingUserDetail,
            )
            HorizontalWeightSpacer(float = 1f)
            Ranker(
                modifier = Modifier.height(120.dp),
                rank = rankBoard.top3[0],
                maxStep = rankBoard.highestStep,
                navigateToRankingUserDetail = navigateToRankingUserDetail,
            )
            HorizontalWeightSpacer(float = 1f)
            Ranker(
                modifier = Modifier.height(90.dp),
                rank = rankBoard.top3[2],
                maxStep = rankBoard.highestStep,
                navigateToRankingUserDetail = navigateToRankingUserDetail,
            )
            HorizontalWeightSpacer(float = 1f)
        }
    }
}

@Composable
private fun Ranker(
    modifier: Modifier = Modifier,
    rank: Rank,
    maxStep: Int,
    navigateToRankingUserDetail: (String, Int) -> Unit,
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(rank.character))
    val lottieProgress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )

    Column(
        modifier = modifier.clickableAvoidingDuplication {
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
        val text = buildAnnotatedString {
            appendFontSizeWithColorText(
                text = rank.designation,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                fontSize = 8.tu,
            )
            append(" ${rank.name}")
        }
        DescriptionAnnotatedSmallText(text = text)
        VerticalSpacer(height = 4.dp)
        FooterText(text = rank.step.toString())
    }
}

@Composable
@Preview
private fun PreviewRankTop3(
    @PreviewParameter(RankBoardPreviewParameter::class)
    rankBoard: RankBoard,
) = StepWalkTheme {
    RankTop3(
        rankBoard = rankBoard,
        dialogState = DialogState.getInitValue(),
        setDialogState = {},
        navigateToRankingUserDetail = { _, _ -> },
    )
}

@Composable
@Preview(showBackground = true)
private fun PreviewRanker(
    @PreviewParameter(RankBoardPreviewParameter::class)
    rankBoard: RankBoard,
) = StepWalkTheme {
    Ranker(
        rank = rankBoard.top3.first(),
        maxStep = rankBoard.highestStep,
        navigateToRankingUserDetail = { _, _ -> },
    )
}