package jinproject.stepwalk.home.component

import android.content.res.Resources
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupPositionProvider
import androidx.compose.ui.window.PopupProperties
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.drawable.toBitmap
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme
import kotlin.math.roundToInt

@Composable
fun PopupWindow(
    text: String,
    popUpState: Boolean,
    popUpOffset: Offset,
    offPopUp: () -> Unit
) {
    if (popUpState) {
        Popup(
            popupPositionProvider = object : PopupPositionProvider {
                override fun calculatePosition(
                    anchorBounds: IntRect,
                    windowSize: IntSize,
                    layoutDirection: LayoutDirection,
                    popupContentSize: IntSize
                ): IntOffset {
                    return IntOffset(
                        x = popUpOffset.x.toInt() + 10f.toInt(),
                        y = popUpOffset.y.toInt() - popupContentSize.height - 25
                    )
                }
            },
            properties = PopupProperties(),
            onDismissRequest = offPopUp
        ) {
            PopUpItem(
                text = text,
                modifier = Modifier
                    .drawBehind {
                        val size = this.size
                        val stroke = Stroke(
                            width = 2.dp.toPx(),
                            pathEffect = PathEffect.cornerPathEffect(4.dp.toPx())
                        )

                        val brush = Brush.verticalGradient(
                            colors = listOf(
                                StepWalkColor.blue_700.color,
                                StepWalkColor.blue_600.color,
                                StepWalkColor.blue_500.color,
                                StepWalkColor.blue_400.color,
                                StepWalkColor.blue_300.color
                            )
                        )
                        val rect = Rect(Offset.Zero, size)
                        val path = Path().apply {
                            moveTo(rect.topLeft.x, rect.topLeft.y)
                            lineTo(rect.bottomLeft.x, rect.bottomLeft.y)
                            lineTo(rect.bottomLeft.x, rect.bottomLeft.y + 15f)
                            lineTo(rect.bottomLeft.x + 20f, rect.bottomLeft.y)
                            lineTo(rect.bottomRight.x, rect.bottomRight.y)
                            lineTo(rect.topRight.x, rect.topRight.y)
                            lineTo(rect.topLeft.x, rect.topLeft.y)
                            close()
                        }
                        val fillPath = Path().apply {
                            addPath(path)
                            close()
                        }

                        drawPath(path, brush = brush, style = stroke)
                        drawPath(fillPath, brush = brush, style = Fill)
                    }
                    .padding(horizontal = 4.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun PopUpItem(
    text: String,
    modifier: Modifier = Modifier
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