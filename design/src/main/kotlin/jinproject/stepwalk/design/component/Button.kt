package jinproject.stepwalk.design.component

import android.os.SystemClock
import androidx.annotation.DrawableRes
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.theme.StepWalkTheme

@Composable
fun DefaultIconButton(
    modifier: Modifier = Modifier,
    @DrawableRes icon: Int,
    onClick: () -> Unit,
    enabled: Boolean = true,
    iconTint: Color,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
) {

    val avoidDuplicationClickEvent = remember {
        AvoidDuplicationClickEvent(onClick)
    }

    IconButton(
        onClick = avoidDuplicationClickEvent::onClick,
        modifier = modifier,
        interactionSource = interactionSource,
        enabled = enabled,
    ) {
        Icon(
            painter = painterResource(id = icon),
            contentDescription = "Default Icon Button",
            tint = iconTint
        )
    }
}

@Composable
fun DefaultButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    shape: RoundedCornerShape = RoundedCornerShape(100.dp),
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentPaddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    content: @Composable () -> Unit,
) {
    val avoidDuplicationClickEvent = remember {
        AvoidDuplicationClickEvent(onClick)
    }

    Column(
        modifier = modifier
            .clip(shape)
            .background(backgroundColor)
            .padding(contentPaddingValues)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = avoidDuplicationClickEvent::onClick
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun DefaultCombinedButton(
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    onLongClick: () -> Unit,
    onDoubleClick: () -> Unit,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() },
    backgroundColor: Color = MaterialTheme.colorScheme.primary,
    contentPaddingValues: PaddingValues = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
    content: @Composable () -> Unit,
) {
    val avoidDuplicationClickEvent = remember {
        AvoidDuplicationClickEvent(onClick)
    }

    Column(
        modifier = modifier
            .clip(RoundedCornerShape(100.dp))
            .background(backgroundColor)
            .padding(contentPaddingValues)
            .combinedClickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = avoidDuplicationClickEvent::onClick,
                onLongClick = onLongClick,
                onDoubleClick = onDoubleClick,
            ),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        content()
    }
}

@Composable
fun DefaultTextButton(
    modifier: Modifier = Modifier,
    text: String,
    textColor : Color,
    enabled: Boolean = true,
    onClick : () -> Unit,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
){
    val avoidDuplicationClickEvent = remember(onClick) {
        AvoidDuplicationClickEvent(onClick)
    }
    TextButton(
        modifier = modifier,
        onClick = avoidDuplicationClickEvent::onClick,
        enabled = enabled,
        interactionSource = interactionSource
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodySmall,
            color = textColor
        )
    }
}

@Stable
private class AvoidDuplicationClickEvent(
    private val onClicked: () -> Unit,
) {
    val currentClickTime get() = SystemClock.uptimeMillis()
    var lastClickTime = currentClickTime

    fun onClick() {
        val elapsedTime = currentClickTime - lastClickTime
        lastClickTime = currentClickTime

        if (elapsedTime <= MIN_CLICK_INTERVAL) {
            return
        }

        onClicked()
    }

    companion object {
        const val MIN_CLICK_INTERVAL = 300L
    }
}

@Preview
@Composable
private fun PreviewDefaultIconButton() = StepWalkTheme {
    DefaultIconButton(
        icon = R.drawable.ic_arrow_left_small,
        onClick = {},
        iconTint = MaterialTheme.colorScheme.onSurface,
    )
}

@Preview()
@Composable
private fun PreviewDefaultButton() =
    StepWalkTheme {
        DefaultButton(
            onClick = {},
            content = {}
        )
    }

