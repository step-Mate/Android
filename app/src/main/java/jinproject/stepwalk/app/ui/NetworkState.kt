package jinproject.stepwalk.app.ui

sealed interface NetworkState {
    data object Loading: NetworkState
    data object Success: NetworkState

    data class Fail(val e: Throwable): NetworkState
}