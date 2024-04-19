package com.stepmate.data.remote.utils

import com.stepmate.data.remote.dto.response.ErrorResponse
import com.stepmate.domain.model.exception.StepMateHttpException
import kotlinx.coroutines.coroutineScope
import retrofit2.Response
import retrofit2.Retrofit

suspend fun <R> suspendAndCatchStepMateData(retrofit: Retrofit, getResponse: suspend () -> Response<R>) = coroutineScope {
    val response = getResponse()

    if(!response.isSuccessful){
        response.errorBody()?.let { errorBody ->
            val errorResponse = retrofit.responseBodyConverter<ErrorResponse>(
                ErrorResponse::class.java,
                ErrorResponse::class.java.annotations).convert(errorBody)!!

            throw StepMateHttpException(message = errorResponse.message, code = errorResponse.code)
        }
    } else
        response.body()
}