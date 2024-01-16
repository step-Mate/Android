package jinproject.stepwalk.home.screen.home.component

import androidx.compose.animation.core.MutableTransitionState
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.tween
import androidx.compose.animation.core.updateTransition
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
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
import jinproject.stepwalk.design.component.VerticalSpacer
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import jinproject.stepwalk.home.screen.home.state.Time

@Stable
internal data class PopUpState(
    val enabled: Boolean,
    val index: Int,
) {
    companion object {
        fun getInitValues() = PopUpState(
            enabled = false,
            index = -1,
        )
    }
}

@Composable
internal fun HomePopUp(
    popUpState: Boolean,
    offPopUp: () -> Unit,
    onClickPopUpItem: (Time) -> Unit,
) {
    val transitionState = remember {
        MutableTransitionState(false)
    }
    transitionState.targetState = popUpState
    val transition = updateTransition(transitionState, label = "PopupTransition")

    val scale by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(durationMillis = 300)
            } else {
                tween(durationMillis = 250)
            }
        },
        label = "PopupScale"
    ) {
        if (it) {
            1f
        } else {
            0f
        }
    }

    val alpha by transition.animateFloat(
        transitionSpec = {
            if (false isTransitioningTo true) {
                tween(durationMillis = 300)
            } else {
                tween(durationMillis = 250)
            }
        },
        label = "PopupAlpha"
    ) {
        if (it) {
            1f
        } else {
            0f
        }
    }

    if (transitionState.currentState || transitionState.targetState) {
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
                        scaleX = scale
                        scaleY = scale
                        this.alpha = alpha
                    }
                    .width(100.dp)
                    .shadow(5.dp, RoundedCornerShape(10.dp))
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Time.values.forEachIndexed { index, time ->
                    Text(
                        text = time.display(),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground,
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

@Composable
private fun PopUpItem(
    text: String,
    modifier: Modifier = Modifier,
) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall,
        modifier = modifier,
    )
}

@Preview
@Composable
private fun PreviewPopUpItem() = StepWalkTheme {
    PopUpItem(text = "305")
}

@Preview(widthDp = 400, heightDp = 830)
@Composable
private fun PreviewHomePopUp() = StepWalkTheme {
    HomePopUp(
        popUpState = true,
        offPopUp = {},
        onClickPopUpItem = {}
    )
}