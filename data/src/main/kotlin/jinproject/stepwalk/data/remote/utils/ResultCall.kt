package jinproject.stepwalk.data.remote.utils

import jinproject.stepwalk.data.remote.dto.response.ApiResponse
import jinproject.stepwalk.data.remote.dto.response.ErrorResponse
import jinproject.stepwalk.domain.model.ResponseState
import jinproject.stepwalk.domain.model.onException
import jinproject.stepwalk.domain.model.onSuccess
import okhttp3.Request
import okio.IOException
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import retrofit2.Retrofit
import java.net.SocketTimeoutException

class ResultCall<T>(private val call: Call<T>, private val retrofit: Retrofit) : Call<ResponseState<T>> {
    override fun enqueue(callback: Callback<ResponseState<T>>) {
        call.enqueue(object : Callback<T> {
            override fun onResponse(call: Call<T>, response: Response<T>) {
                if (response.isSuccessful) {
                    if(response.body() == null) {
                        callback.onResponse(this@ResultCall, Response.success(ResponseState.Exception(1, "body가 비었습니다")))
                    }
                    else {
                        callback.onResponse(this@ResultCall, Response.success(ResponseState.Result(response.body()!!)))
                    }
                } else {
                    if(response.errorBody() == null) {
                        callback.onResponse( this@ResultCall, Response.success(ResponseState.Exception(0, "errorBody가 비었습니다")))
                    }
                    else {
                        val errorBody = retrofit.responseBodyConverter<ErrorResponse>(
                            ErrorResponse::class.java,
                            ErrorResponse::class.java.annotations
                        ).convert(response.errorBody()!!)

                        val message: String = errorBody?.message ?: "errorBody가 비었습니다"
                        val code : Int = errorBody?.code ?: 0
                        callback.onResponse(this@ResultCall, Response.success(ResponseState.Exception(code, message)))
                    }
                }
            }

            override fun onFailure(call: Call<T>, t: Throwable) {
                callback.onResponse(
                    this@ResultCall,
                    Response.success(when (t) {
                        is HttpException -> {
                            ResponseState.Exception(903, "서버에 문제가 발생하였습니다")
                        }
                        is SocketTimeoutException -> {
                            ResponseState.Exception(900, "서버에 연결할 수 없습니다.")
                        }
                        is IOException -> {
                            ResponseState.Exception(901, "인터넷 연결이 끊겼습니다.")
                        }
                        else -> {
                            ResponseState.Exception(902, "원인불명에 장애가 발생하였습니다.")
                        }
                    })
                )
            }
        })
    }

    override fun isExecuted(): Boolean {
        return call.isExecuted
    }

    override fun execute(): Response<ResponseState<T>> {
        return Response.success(ResponseState.Result(call.execute().body()!!))
    }

    override fun cancel() {
        call.cancel()
    }

    override fun isCanceled(): Boolean {
        return call.isCanceled
    }

    override fun clone(): Call<ResponseState<T>> {
        return ResultCall(call.clone(), retrofit)
    }

    override fun request(): Request {
        return call.request()
    }

    override fun timeout(): Timeout {
        return call.timeout()
    }
}

fun <T> ResponseState<ApiResponse<T>>.getResult() : ResponseState<T> {
    this.onSuccess {
        return ResponseState.Result(it?.result)
    }.onException { code, message ->
        return ResponseState.Exception(code, message)
    }
    return ResponseState.Exception(0, "")
}

fun <T,E> ResponseState<ApiResponse<T>>.getResult(
    trans : (T?) -> ResponseState<E>
) : ResponseState<E> {
    this.onSuccess {
        return trans(it?.result)
    }.onException { code, message ->
        return ResponseState.Exception(code, message)
    }
    return ResponseState.Exception(0, "")
}