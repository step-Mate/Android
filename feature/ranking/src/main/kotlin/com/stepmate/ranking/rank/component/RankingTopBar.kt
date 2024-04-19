package com.stepmate.ranking.rank.component

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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.stepmate.design.R
import com.stepmate.design.appendFontSizeWithColorText
import com.stepmate.design.component.DefaultIconButton
import com.stepmate.design.component.DescriptionAnnotatedSmallText
import com.stepmate.design.component.DescriptionLargeText
import com.stepmate.design.component.DescriptionSmallText
import com.stepmate.design.component.FooterText
import com.stepmate.design.component.HorizontalSpacer
import com.stepmate.design.component.HorizontalWeightSpacer
import com.stepmate.design.component.VerticalSpacer
import com.stepmate.design.theme.StepWalkColor
import com.stepmate.design.theme.StepMateTheme
import com.stepmate.design.tu
import com.stepmate.ranking.detail.User
import com.stepmate.ranking.detail.UserDetailPreviewParameter
import com.stepmate.ranking.rank.Rank
import java.math.RoundingMode
import java.text.DecimalFormat

@Composable
internal fun RankingTopBar(
    modifier: Modifier = Modifier,
    user: Rank,
    maxStep: Int,
    navigateToNoti: () -> Unit,
    isRequestedFriend: Boolean,
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
                iconSize = 32.dp,
                modifier = Modifier.drawWithContent {
                    drawContent()
                    if(isRequestedFriend)
                        drawCircle(StepWalkColor.red_500.color, 10f, center = this.center.copy(y = this.center.y - 30f))
                }
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
) = StepMateTheme {
    RankingTopBar(
        user = user.info,
        maxStep = 3000,
        navigateToNoti = {},
        isRequestedFriend = true,
    )
}