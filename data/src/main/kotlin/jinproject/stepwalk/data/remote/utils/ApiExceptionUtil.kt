package jinproject.stepwalk.data.remote.utils

import jinproject.stepwalk.domain.model.ResponseState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

suspend inline fun <T> exchangeResultFlow(
    crossinline action : suspend CoroutineScope.() -> ResponseState<T>
) : Flow<ResponseState<T>> = callbackFlow {
    trySendBlocking(ResponseState.Loading)
    trySendBlocking(action())
    awaitClose()
}