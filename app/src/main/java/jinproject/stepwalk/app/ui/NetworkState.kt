package jinproject.stepwalk.app.ui

sealed interface NetworkState {
    data object Loading: NetworkState
    data object Success: NetworkState
}