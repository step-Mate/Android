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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
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
import jinproject.stepwalk.design.theme.StepWalkTheme

@Composable
fun PopupWindow(
    value: Long,
    popUpState: Boolean,
    popUpOffset: Offset,
    resources: Resources = LocalContext.current.resources,
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
                        x = popUpOffset.x.toInt(),
                        y = popUpOffset.y.toInt() - popupContentSize.height
                    )
                }
            },
            properties = PopupProperties(),
            onDismissRequest = offPopUp
        ) {
            PopUpItem(
                text = value.toString(),
                modifier = Modifier
                    .size(40.dp)
                    .drawWithCache {
                        onDrawBehind {
                            drawImage(
                                image = ResourcesCompat
                                    .getDrawable(
                                        resources,
                                        jinproject.stepwalk.design.R.drawable.ic_speeach_bubble,
                                        null
                                    )!!
                                    .toBitmap(
                                        width = 40.dp.roundToPx(),
                                        height = 40.dp.roundToPx()
                                    )
                                    .asImageBitmap(),
                            )
                        }
                    }
            )
        }
    }
}

@Composable
private fun PopUpItem(
    text: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Preview
@Composable
private fun PreviewPopUpItem() = StepWalkTheme {
    PopUpItem(text = "305")
}