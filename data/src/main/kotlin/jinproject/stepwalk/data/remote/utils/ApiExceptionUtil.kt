package jinproject.stepwalk.data.remote.utils

import android.util.Log
import jinproject.stepwalk.domain.model.ResponseState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okio.IOException
import retrofit2.HttpException
import java.net.SocketTimeoutException

suspend inline fun <T> checkApiException(crossinline callFunction: suspend () -> ResponseState<T>): ResponseState<T> =
    try{
        withContext(Dispatchers.IO){
            callFunction()
        }
    }catch (e: Exception){
        withContext(Dispatchers.Main){
            e.printStackTrace()
            Log.e("apiCallError", "Call error: ${e.localizedMessage}", e.cause)
            when(e){
                is HttpException -> { ResponseState.Exception(e.code(),e.message()) }
                is SocketTimeoutException -> { ResponseState.Exception(900,"서버에 연결할 수 없습니다.")}
                is IOException -> { ResponseState.Exception(901,"일시적인 장애가 발생하였습니다.")}
                else -> { ResponseState.Exception(902,"원인불명에 장애가 발생하였습니다.")}
            }
        }
    }