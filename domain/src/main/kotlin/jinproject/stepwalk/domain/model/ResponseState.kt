package jinproject.stepwalk.domain.model

sealed interface ResponseState<out T> {
    data object Loading : ResponseState<Nothing>
    data class Result<out T>(val data : T) : ResponseState<T>
    data class Exception(val code : Int, val message : String) : ResponseState<Nothing>
}

inline fun <reified T : Any> ResponseState<T>.onSuccess(action : (data : T) -> Unit) : ResponseState<T> {
    if (this is ResponseState.Result) action(data)
    return this
}

inline fun <reified T : Any> ResponseState<T>.onException(action : (code : Int, message : String) -> Unit) : ResponseState<T> {
    if (this is ResponseState.Exception) action(code,message)
    return this
}

fun <T> transResponseState(code: Int, message: String, result : T) : ResponseState<T> =
    if (code == 200) ResponseState.Result(result) else ResponseState.Exception(code,message)