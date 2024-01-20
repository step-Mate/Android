package jinproject.stepwalk.design.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.SnackbarHost
import androidx.compose.material.SnackbarHostState
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Snackbar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import jinproject.stepwalk.design.R
import jinproject.stepwalk.design.theme.StepWalkTheme

@Composable
fun StepMateSnackBar(
    modifier: Modifier = Modifier,
    snackBarHostState: SnackbarHostState,
    headerMessage: String,
    contentMessage: String,
    dismissSnackBar: () -> Unit,
) {
    Column {
        SnackbarHost(
            hostState = snackBarHostState,
            modifier = modifier,
            snackbar = {
                SnackBarCustom(
                    headerMessage = headerMessage,
                    contentMessage = contentMessage,
                    dismissSnackBar = dismissSnackBar,
                )
            }
        )
        VerticalSpacer(height = 90.dp)
    }
}

@Composable
private fun SnackBarCustom(
    headerMessage: String,
    contentMessage: String,
    dismissSnackBar: () -> Unit,
) {
    Snackbar(
        modifier = Modifier
            .padding(horizontal = 16.dp)
            .height(78.dp),
        containerColor = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        actionContentColor = MaterialTheme.colorScheme.onPrimary,
        shape = RoundedCornerShape(10.dp),
        action = {
            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .wrapContentHeight()
            ) {
                DefaultIconButton(
                    modifier = Modifier,
                    icon = R.drawable.ic_x,
                    onClick = dismissSnackBar,
                    iconTint = MaterialTheme.colorScheme.onSurfaceVariant,
                    backgroundTint = MaterialTheme.colorScheme.primary
                )
            }
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            DescriptionLargeText(
                text = headerMessage,
                color = MaterialTheme.colorScheme.onPrimary
            )
            if (contentMessage.isNotBlank()) {
                VerticalSpacer(height = 4.dp)
                DescriptionSmallText(
                    text = contentMessage,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        }
    }
}

@Preview
@Composable
private fun PreviewSnackBarCustom1() = StepWalkTheme {
    SnackBarCustom(
        headerMessage = "헤더메세지는 이렇게 보입니다.",
        contentMessage = "컨텐트메세지는 이렇게 보입니다.",
        dismissSnackBar = {}
    )
}

@Preview
@Composable
private fun PreviewSnackBarCustom2() = StepWalkTheme {
    SnackBarCustom(
        headerMessage = "컨텐트메세지가 없다면 이렇게 보입니다.",
        contentMessage = "",
        dismissSnackBar = {}
    )
}