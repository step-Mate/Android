package com.stepmate.data.remote.utils

import com.stepmate.domain.model.ResponseState
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

        if (upperBound !is ParameterizedType && getRawType(upperBound) != ResponseState::class.java)
            return null

        return object : CallAdapter<Type, Call<ResponseState<Type>>> {
            override fun responseType(): Type = getParameterUpperBound(0, upperBound as ParameterizedType)

            override fun adapt(call: Call<Type>): Call<ResponseState<Type>> =
                ResultCall(call, retrofit)
        }
    }
}