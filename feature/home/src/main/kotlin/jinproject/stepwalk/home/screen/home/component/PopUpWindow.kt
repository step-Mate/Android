package jinproject.stepwalk.home.screen.home.component

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import jinproject.stepwalk.design.component.DescriptionSmallText
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.home.state.Time

@Composable
internal fun HomePopUp(
    popUpState: Boolean,
    offPopUp: () -> Unit,
    onClickPopUpItem: (Time) -> Unit,
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
                        x = 20,
                        y = 200
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
                Time.values.forEachIndexed { index, time ->
                    DescriptionSmallText(
                        text = time.display(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onClickPopUpItem(time)
                            }
                    )
                    if (index != Time.values.lastIndex) {
                        VerticalSpacer(height = 10.dp)
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomePopUp() = StepWalkTheme {
    Column(
        Modifier
            .fillMaxWidth()
            .height(300.dp)
    ) {
        HomePopUp(
            popUpState = true,
            offPopUp = {},
            onClickPopUpItem = {}
        )
    }
}