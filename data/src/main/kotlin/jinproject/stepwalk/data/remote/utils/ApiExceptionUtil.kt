package jinproject.stepwalk.data.remote.utils

import jinproject.stepwalk.domain.model.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.trySendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException
import java.net.SocketTimeoutException

suspend inline fun <T> checkApiException(
    crossinline callFunction: suspend () -> ResponseState<T>
): ResponseState<T> =
    try{
        withContext(Dispatchers.IO){
            callFunction()
        }
    }catch (e: Exception){
        withContext(Dispatchers.Main){
            e.printStackTrace()
            when(e){
                is HttpException -> { ResponseState.Exception(e.code(),e.message()) }
                is SocketTimeoutException -> { ResponseState.Exception(900,"서버에 연결할 수 없습니다.")}
                is IOException -> { ResponseState.Exception(901,"일시적인 장애가 발생하였습니다.")}
                else -> { ResponseState.Exception(902,"원인불명에 장애가 발생하였습니다.")}
            }
        }
    }

suspend inline fun <T> exchangeResultFlow(
    crossinline action : suspend () -> ResponseState<T>
) : Flow<ResponseState<T>> = callbackFlow {
    trySendBlocking(ResponseState.Loading)
    trySendBlocking(checkApiException {
        action()
    })
    awaitClose()
}