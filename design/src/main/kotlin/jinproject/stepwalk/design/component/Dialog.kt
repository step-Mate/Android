package jinproject.stepwalk.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import jinproject.stepwalk.design.theme.StepWalkColor
import jinproject.stepwalk.design.theme.StepWalkTheme

@Stable
data class DialogState(
    val header: String,
    val content: String = "",
    val positiveMessage: String,
    val negativeMessage: String = "",
    val onPositiveCallback: () -> Unit,
    val onNegativeCallback: () -> Unit = {},
    val isShown: Boolean = false,
) {
    companion object {
        fun getInitValue() = DialogState(
            header = "",
            content = "",
            positiveMessage = "",
            negativeMessage = "",
            onPositiveCallback = {},
            onNegativeCallback = {},
        )
    }
}

@Composable
fun StepMateDialog(
    dialogState: DialogState,
    properties: DialogProperties = DialogProperties(),
    hideDialog: () -> Unit,
) {
    if (dialogState.isShown)
        Dialog(
            onDismissRequest = {
                hideDialog()
            },
            properties = properties
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier,
            ) {
                StepMateDialogContent(
                    dialogState = dialogState,
                    onPositiveCallback = dialogState.onPositiveCallback,
                    onNegativeCallback = dialogState.onNegativeCallback
                )
            }
        }
}

@Composable
private fun StepMateDialogContent(
    dialogState: DialogState,
    onPositiveCallback: () -> Unit,
    onNegativeCallback: () -> Unit = {},
) {
    Column(
        modifier = Modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        VerticalSpacer(height = 12.dp)
        DescriptionLargeText(text = dialogState.header)
        VerticalSpacer(height = 12.dp)
        if (dialogState.content.isNotBlank())
            DescriptionSmallText(
                text = dialogState.content,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        VerticalSpacer(height = 12.dp)
        HorizontalDivider()
        Row(
            modifier = Modifier.height(44.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (dialogState.negativeMessage.isNotBlank()) {
                DescriptionSmallText(
                    text = dialogState.negativeMessage,
                    color = StepWalkColor.red_400.color,
                    modifier = Modifier
                        .clickableAvoidingDuplication {
                            onNegativeCallback()
                        }
                        .weight(1f),
                    textAlign = TextAlign.Center,
                )
                VerticalDivider()
            }
            DescriptionSmallText(
                text = dialogState.positiveMessage,
                color = StepWalkColor.blue_400.color,
                modifier = Modifier
                    .clickableAvoidingDuplication {
                        onPositiveCallback()
                    }
                    .weight(1f),
                textAlign = TextAlign.Center,
            )
        }
    }
}

@Composable
@Preview
private fun PreviewDialog() {
    StepWalkTheme {
        StepMateDialog(
            dialogState = DialogState(
                header = "헤더메세지는 이렇게 나옵니다.",
                content = "컨텐트메세지는 이렇게 나옵니다.",
                positiveMessage = "네",
                negativeMessage = "아뇨",
                onPositiveCallback = {},
                onNegativeCallback = {},
                isShown = true
            ),
            hideDialog = {},
        )
    }
}

@Composable
@Preview
private fun PreviewDialogWithoutNegative() {
    StepWalkTheme {
        StepMateDialog(
            dialogState = DialogState(
                header = "헤더메세지는 이렇게 나옵니다.",
                content = "컨텐트메세지는 이렇게 나옵니다.",
                positiveMessage = "확인",
                negativeMessage = "",
                onPositiveCallback = {},
                onNegativeCallback = {},
                isShown = true
            ),
            hideDialog = {},
        )
    }
}