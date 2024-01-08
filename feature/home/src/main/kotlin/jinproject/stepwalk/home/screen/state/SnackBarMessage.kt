package jinproject.stepwalk.home.screen.state

import androidx.compose.runtime.Stable

@Stable
class SnackBarMessage(
    val headerMessage: String,
    val contentMessage: String = ""
) {
    companion object {
        fun getInitValues() = SnackBarMessage(
            headerMessage = "",
            contentMessage = ""
        )
    }
}
