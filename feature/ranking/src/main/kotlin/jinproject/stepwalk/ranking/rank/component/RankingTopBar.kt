package jinproject.stepwalk.ranking.rank.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.appendFontSizeWithColorText
import jinproject.stepwalk.design.component.DefaultIconButton
import jinproject.stepwalk.design.component.DescriptionAnnotatedSmallText
import jinproject.stepwalk.design.component.DescriptionLargeText
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.FooterText
import jinproject.stepwalk.design.component.HorizontalSpacer
import jinproject.stepwalk.design.component.HorizontalWeightSpacer
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.design.tu
import jinproject.stepwalk.ranking.detail.User
import jinproject.stepwalk.ranking.detail.UserDetailPreviewParameter
import jinproject.stepwalk.ranking.rank.Rank
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
internal fun RankingTopBar(
    modifier: Modifier = Modifier,
    user: Rank,
    maxStep: Int,
    navigateToNoti: () -> Unit,
) {
    Column(
        modifier = modifier
            .shadow(4.dp, RectangleShape, clip = false)
            .background(MaterialTheme.colorScheme.surface)
            .windowInsetsPadding(WindowInsets.statusBars)
            .padding(horizontal = 16.dp, vertical = 12.dp),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
        ) {
            HorizontalWeightSpacer(float = 1f)
            DefaultIconButton(
                icon = R.drawable.ic_notification,
                onClick = navigateToNoti,
                iconTint = MaterialTheme.colorScheme.onSurface,
                iconSize = 32.dp
            )
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom
        ) {
            FooterText(
                text = user.designation,
            )
            HorizontalSpacer(width = 4.dp)
            DescriptionLargeText(text = user.name)
            DescriptionSmallText(text = "님 화이팅!")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.Bottom,
        ) {
            val text = buildAnnotatedString {
                append("지금까지 지구 ")
                appendFontSizeWithColorText(
                    text = DecimalFormat("#.##").apply { roundingMode = RoundingMode.CEILING }
                        .format(user.step * 0.0008f / 40075),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    fontSize = 14.tu,
                )
                append(" 바퀴를 도셨어요.")
            }
            DescriptionAnnotatedSmallText(text = text)
            HorizontalWeightSpacer(float = 1f)
            RankNumber(
                rank = user,
            )
        }
        VerticalSpacer(height = 4.dp)
        UserCharacterWithStepProgress(
            rank = user,
            maxStep = maxStep,
        )
    }
}

@Composable
@Preview(showBackground = true)
private fun PreviewRankingTopBar(
    @PreviewParameter(UserDetailPreviewParameter::class)
    user: User,
) = StepWalkTheme {
    RankingTopBar(
        user = user.info,
        maxStep = 3000,
        navigateToNoti = {},
    )
}