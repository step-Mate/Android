package jinproject.stepwalk.login.screen.state

import androidx.compose.runtime.Stable

@Stable
data class AuthState(
    val errorMessage: String = "",
    val isSuccess: Boolean = false,
    val isLoading: Boolean = false
)
