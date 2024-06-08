package com.stepmate.profile.screen.profile.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.airbnb.lottie.compose.LottieAnimation
import com.airbnb.lottie.compose.LottieCompositionSpec
import com.airbnb.lottie.compose.LottieConstants
import com.airbnb.lottie.compose.animateLottieCompositionAsState
import com.airbnb.lottie.compose.rememberLottieComposition
import com.stepmate.design.component.DescriptionSmallText
import com.stepmate.design.component.FooterText
import com.stepmate.design.component.HorizontalSpacer
import com.stepmate.domain.model.BodyData
import com.stepmate.domain.model.user.User

@Composable
internal fun ProfileDetail(
    modifier: Modifier = Modifier,
    user: User,
    bodyData: BodyData,
    backgroundColor: Color = MaterialTheme.colorScheme.surface,
    circleColor: Color = MaterialTheme.colorScheme.primaryContainer
) {
    val composition by rememberLottieComposition(LottieCompositionSpec.Asset(user.character))
    val lottieProgress by animateLottieCompositionAsState(
        composition,
        iterations = LottieConstants.IterateForever
    )
    val textStyle = MaterialTheme.typography.bodyMedium
    val textMeasurer = rememberTextMeasurer()
    val textLayoutResult = remember(user.level.toString(), textStyle) {
        textMeasurer.measure("LV.${user.level}", textStyle)
    }

    Box(
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .shadow(elevation = 6.dp, shape = RoundedCornerShape(8.dp))
                .background(color = MaterialTheme.colorScheme.surface)
                .align(Alignment.CenterStart)
                .fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .padding(start = 30.dp, top = 50.dp)
                    .height(IntrinsicSize.Min),
                verticalAlignment = Alignment.Bottom,
            ) {
                FooterText(
                    text = user.designation.ifEmpty { "뉴비" },
                    color = MaterialTheme.colorScheme.onSurface
                )
                HorizontalSpacer(width = 4.dp)
                DescriptionSmallText(
                    text = if (user.name.isEmpty()) "" else "${user.name} 님",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }

            Row(
                verticalAlignment = Alignment.Bottom,
                modifier = Modifier
                    .padding(start = 30.dp, top = 10.dp, bottom = 20.dp)
                    .height(IntrinsicSize.Min)
            ) {
                DescriptionSmallText(
                    text = "${bodyData.age} 세",
                    color = MaterialTheme.colorScheme.onSurface
                )
                HorizontalSpacer(width = 4.dp)
                DescriptionSmallText(
                    text = "${bodyData.height} cm",
                    color = MaterialTheme.colorScheme.onSurface
                )
                HorizontalSpacer(width = 4.dp)
                DescriptionSmallText(
                    text = "${bodyData.weight} kg",
                    color = MaterialTheme.colorScheme.onSurface
                )
            }
        }
        LottieAnimation(
            composition = composition,
            progress = { lottieProgress },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .offset(y = (-90).dp)
                .size(110.dp)
                .drawBehind {
                    drawCircle(
                        color = backgroundColor,
                        radius = 190.dp.value
                    )
                    drawCircle(
                        color = circleColor,
                        radius = 190.dp.value,
                        style = Stroke(width = 5f)
                    )
                    drawText(
                        textLayoutResult = textLayoutResult,
                        topLeft = Offset(
                            x = center.x - textLayoutResult.size.width / 2,
                            y = this.size.height * 0.85f
                        )
                    )
                }
        )
    }
}

@Preview
@Composable
private fun PreviewProfileDetail(

) {
    ProfileDetail(
        user = User.getInitValues(),
        bodyData = BodyData()
    )
}