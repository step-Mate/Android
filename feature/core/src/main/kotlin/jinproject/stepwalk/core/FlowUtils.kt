package jinproject.stepwalk.core


import jinproject.stepwalk.domain.model.exception.StepMateHttpException
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import java.io.IOException
import java.net.SocketTimeoutException

fun <T> Flow<T>.catchDataFlow(action: (StepMateHttpException) -> Throwable, onException: suspend (Throwable) -> Unit,) = this.catch { e ->
    val exception = when (e) {
        is SocketTimeoutException -> {
            IllegalStateException("[SocketTimeOutException] 로 인해 요청을 수행할 수 없어요.")
        }

        is IOException -> {
            IllegalStateException("[IOException] 로 인해 요청을 수행할 수 없어요.")
        }

        is StepMateHttpException -> {
            action(e)
        }

        else -> {
            IllegalStateException("[${e.message}] 로 인해 요청을 수행할 수 없어요.")
        }
    }

    onException(exception)
}