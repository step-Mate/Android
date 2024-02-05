package jinproject.stepwalk.data.remote.utils

import jinproject.stepwalk.domain.model.ResponseState
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ResultCallAdapterFactory: CallAdapter.Factory() {
    override fun get(
        returnType: Type,
        annotations: Array<out Annotation>,
        retrofit: Retrofit
    ): CallAdapter<*, *>? {
        if (getRawType(returnType) != Call::class.java || returnType !is ParameterizedType) {
            return null
        }
        val upperBound = getParameterUpperBound(0, returnType)

        return if (upperBound is ParameterizedType && upperBound.rawType == ResponseState::class.java) {
            object : CallAdapter<Any, Call<ResponseState<*>>> {
                override fun responseType(): Type = getParameterUpperBound(0, upperBound)

                override fun adapt(call: Call<Any>): Call<ResponseState<*>> =
                    ResultCall(call, retrofit) as Call<ResponseState<*>>
            }
        } else {
            null
        }
    }
}