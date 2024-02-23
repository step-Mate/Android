package jinproject.stepwalk.data.remote.utils

import jinproject.stepwalk.domain.model.exception.StepMateHttpException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import retrofit2.HttpException

fun <R> stepMateDataFlow(remoteApi: suspend () -> R) = flowOf(true).map { remoteApi() }.convertHttpException()

fun <T> Flow<T>.convertHttpException() = this.catch { e ->
    if (e is HttpException)
        throw StepMateHttpException(message = e.message(), code = e.code())
    else
        throw e
}